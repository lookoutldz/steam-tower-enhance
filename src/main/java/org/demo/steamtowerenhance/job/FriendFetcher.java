package org.demo.steamtowerenhance.job;

import okhttp3.Request;
import okhttp3.Response;
import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.Friend;
import org.demo.steamtowerenhance.dto.steamresponse.GetFriendListResponse;
import org.demo.steamtowerenhance.service.FriendService;
import org.demo.steamtowerenhance.service.PlayerService;
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

    private final ConcurrentHashMap<String, Integer> failedMap = new ConcurrentHashMap<>();

    private final SteamWebApi steamWebApi;
    private final HttpUtils httpUtils;
    private final FriendService friendService;
    private final PlayerService playerService;
    private final CommonThreadPool commonThreadPool;

    public FriendFetcher(SteamWebApi steamWebApi,
                         HttpUtils httpUtils,
                         FriendService friendService,
                         PlayerService playerService,
                         CommonThreadPool commonThreadPool) {
        this.steamWebApi = steamWebApi;
        this.httpUtils = httpUtils;
        this.friendService = friendService;
        this.playerService = playerService;
        this.commonThreadPool = commonThreadPool;
    }

    /**
     * 批量获取用户好友列表并插入数据库
     * 策略: insert ignore
     */
    @Override
    public void fetchFriends() {
        int total = 0;
        // 1. count steamid from player
        final Integer allPlayerNum = playerService.countAllPlayers();

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
            final List<String> pagedSteamIds = playerService.findPlayerSteamIds(i * pageSize, pageSize);
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
                    final int row = friendConcurrentList.size();
                    if (row > 0) {
                        friendService.insertBatch(friendConcurrentList);
                    }
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

    /**
     * 使用 CompletableFuture 来组织网络IO和数据库IO操作
     * @param steamid steamid
     * @param friendConcurrentList friend list
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> generateFutureTask(String steamid, Collection<Friend> friendConcurrentList) {
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
                    if (throwable != null) {
                        LOGGER.error("Something went wrong: " + throwable.getMessage(), throwable);
                    }
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

    /**
     * 以往更新失败的重新更新
     */
    public void reFetchFailedRecords() {
        final ConcurrentHashMap.KeySetView<String, Integer> keys = failedMap.keySet();
        final int size = keys.size();
        LOGGER.info("failedMap size : " + size);
        if (size < 1) {
            return;
        }
        // 拆分
        final List<List<String>> steamidsList =
                size > MAX_UPDATE_STEAMID
                        ? separateList(keys.stream().toList(), MAX_UPDATE_STEAMID)
                        : List.of(keys.stream().toList());

        for (List<String> steamids : steamidsList) {
            final CopyOnWriteArrayList<Friend> friendConcurrentList = new CopyOnWriteArrayList<>();
            final List<CompletableFuture<Void>> futures = new ArrayList<>(getCapacity(steamids.size()));
            for (String steamid : steamids) {
                futures.add(generateFutureTask(steamid, friendConcurrentList));
            }
            CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            try {
                allOfFuture.get();
                final int row = friendConcurrentList.size();
                if (row > 0) {
                    friendService.insertBatch(friendConcurrentList);
                }
                // 成功后删除failedMap记录
                steamids.forEach(failedMap::remove);
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error while future getting: " + e.getMessage(), e);
                for (String steamid : steamids) {
                    failedMap.put(steamid, FUTURE_ERROR_CODE);
                }
            }
        }
    }
}
