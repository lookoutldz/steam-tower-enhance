package org.demo.steamtowerenhance.job;

import okhttp3.Request;
import okhttp3.Response;
import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.Friend;
import org.demo.steamtowerenhance.dto.steamresponse.GetFriendListResponse;
import org.demo.steamtowerenhance.mapper.FriendMapper;
import org.demo.steamtowerenhance.mapper.PlayerMapper;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

@Component
public class FriendFetcher extends AbstractSteamDataFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendFetcher.class);
    private static final int REQUEST_ERROR_CODE = 999;
    private static final int FUTURE_ERROR_CODE = 998;
    private static final String GET_FRIEND_LIST_CAPTION = "getFriendList";
    private static final String REQUEST_PARAM_STEAMID = "steamid";

    private static final int MAX_UPDATE_STEAMID = 100;

    // TODO 后续处理
    private final ConcurrentHashMap<String, Integer> failedMap = new ConcurrentHashMap<>();

    private final SteamWebApi steamWebApi;
    private final HttpUtils httpUtils;
    private final FriendMapper friendMapper;
    private final PlayerMapper playerMapper;
    private final CommonThreadPool commonThreadPool;

    public FriendFetcher(SteamWebApi steamWebApi,
                         HttpUtils httpUtils,
                         FriendMapper friendMapper,
                         PlayerMapper playerMapper,
                         CommonThreadPool commonThreadPool) {
        this.steamWebApi = steamWebApi;
        this.httpUtils = httpUtils;
        this.friendMapper = friendMapper;
        this.playerMapper = playerMapper;
        this.commonThreadPool = commonThreadPool;
    }

    // TODO 考虑事务
    @Override
    public void fetchFriends() {
        int total = 0;
        // 1. count steamid from player
        final Integer allPlayerNum = playerMapper.countAllPlayers();

        // 2. select steamids partly if necessary
        final int pageSize = 10000;
        final int pageCount;
        if (allPlayerNum > pageSize) {
            pageCount = allPlayerNum / pageSize + 1;
        } else {
            pageCount = 1;
        }

        for (int i = 0; i < pageCount; i++) {
            int rows = 0;
            final List<String> pagedSteamIds = playerMapper.findPlayerSteamIds(i * pageSize, pageSize);
            // 3. fetch api async
            List<List<String>> steamidsList = separateList(pagedSteamIds, MAX_UPDATE_STEAMID);

            for (int j = 0; j < steamidsList.size(); j++) {
                List<String> steamids = steamidsList.get(j);
                // 更新 DB 的最小数据集
                final int capacity = getCapacity(steamids.size());
                final CopyOnWriteArrayList<Friend> friendConcurrentList = new CopyOnWriteArrayList<>();
                final List<CompletableFuture<Void>> futures = new ArrayList<>(capacity);
                for (String steamid : steamids) {
                    // 异步任务
                    futures.add(generateFutureTask(steamid, friendConcurrentList));
                }

                // sync and insert
                CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                try {
                    // 同步
                    allOfFuture.get();
                    friendMapper.insertBatch(friendConcurrentList);
                    final int row = friendConcurrentList.size();
                    LOGGER.debug("Group " + j + " finished, rows = " + row);
                    rows += row;
                    total += rows;
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Error while future getting: " + e.getMessage(), e);
                    for (String steamid : steamids) {
                        failedMap.put(steamid, FUTURE_ERROR_CODE);
                    }
                }
            }
            LOGGER.info("本轮次: " + i +", 查询 steamid: " + pagedSteamIds.size() + ", friendsteamid: " + rows);
        }
        LOGGER.info("本次 fetch 总数据, total: " + total);
    }

    private CompletableFuture<Void> generateFutureTask(String steamid, Collection<Friend> friendConcurrentList) {
        return CompletableFuture
                .supplyAsync(() ->
                        httpUtils.getAsObject(
                                steamWebApi.getFriendList(steamid),
                                GET_FRIEND_LIST_CAPTION,
                                GetFriendListResponse.class,
                                this::requestFailedHandler,
                                this::requestErrorHandler
                        ), commonThreadPool)
                .thenAcceptAsync(getFriendListResponse -> {
                    final List<Friend> friends;
                    if (getFriendListResponse != null
                            && getFriendListResponse.friendslist() != null
                            && (friends = getFriendListResponse.friendslist().friends()) != null) {
                        List<Friend> actualFriends = friends.parallelStream()
                                .filter(friend -> friend.getFriendsteamid() != null)
                                .toList();
                        actualFriends.parallelStream().forEach(friend -> friend.setSteamid(steamid));
                        if (!actualFriends.isEmpty()) {
                            friendConcurrentList.addAll(actualFriends);
                        }
                    }
                })
                .handleAsync((unused, throwable) -> {
                    LOGGER.error("Something went wrong: " + throwable.getMessage(), throwable);
                    return unused;
                });
    }

    private GetFriendListResponse requestFailedHandler(Response response) {
        String steamid = response.request().url().queryParameter(REQUEST_PARAM_STEAMID);
        if (steamid != null) {
            int code = response.code();
            failedMap.put(steamid, code);
        } else {
            LOGGER.warn("Invalid steamid: null");
        }
        return null;
    }

    private void requestErrorHandler(Request request, Throwable throwable) {
        String steamid = request.url().queryParameter(REQUEST_PARAM_STEAMID);
        if (steamid != null) {
            failedMap.put(steamid, REQUEST_ERROR_CODE);
        } else {
            LOGGER.error("Request error: " + throwable.getMessage(), throwable);
        }
    }
}
