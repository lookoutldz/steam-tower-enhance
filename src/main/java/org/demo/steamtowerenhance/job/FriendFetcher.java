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
public class FriendFetcher extends AbstractBaseOnExistingSteamDataFetcher<String, String, Friend, GetFriendListResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendFetcher.class);
    private static final int REQUEST_ERROR_CODE = 999;
    private static final int FUTURE_ERROR_CODE = 998;
    private static final String GET_FRIEND_LIST_CAPTION = "getFriendList";
    private static final String REQUEST_PARAM_STEAMID = "steamid";
    private static final int MAX_SELECT_STEAMID = 100;
    private static final int DB_QUERY_PAGE_SIZE = 10000;

    private final ConcurrentHashMap<String, Integer> failedMap = new ConcurrentHashMap<>();

    private final FriendService friendService;
    private final PlayerService playerService;

    public FriendFetcher(SteamWebApi steamWebApi,
                         HttpUtils httpUtils,
                         CommonThreadPool commonThreadPool,
                         FriendService friendService,
                         PlayerService playerService) {
        super(steamWebApi, httpUtils, commonThreadPool);
        this.friendService = friendService;
        this.playerService = playerService;
    }

    /**
     * 批量获取用户好友列表并插入数据库
     * 策略: insert ignore
     */
    @Override
    public void fetchFriends() {
        process(
                s -> s,
                (rawFriends, steamid) -> {
                    rawFriends.parallelStream().forEach(friend -> friend.setSteamid(steamid));
                    return rawFriends;
                },
                (throwable, steamids) -> {
                    getLogger().error("Failed to fetch friends.", throwable);
                    for (String steamid : steamids) {
                        failedMap.put(steamid, FUTURE_ERROR_CODE);
                    }
                }
        );
    }

    public GetFriendListResponse requestFailedHandler(Response response) {
        String steamid = response.request().url().queryParameter(REQUEST_PARAM_STEAMID);
        final int code = response.code();
        if (steamid != null) {
            failedMap.put(steamid, code);
        } else {
            LOGGER.warn("HTTP " + code + " for null steamid");
        }
        return null;
    }

    public void requestErrorHandler(Request request, Throwable throwable) {
        String steamid = request.url().queryParameter(REQUEST_PARAM_STEAMID);
        if (steamid != null) {
            failedMap.put(steamid, REQUEST_ERROR_CODE);
        } else {
            if (throwable != null) {
                LOGGER.error("Request error: " + throwable.getMessage(), throwable);
            }
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
                size > MAX_SELECT_STEAMID
                        ? separateList(keys.stream().toList(), MAX_SELECT_STEAMID)
                        : List.of(keys.stream().toList());

        for (List<String> steamids : steamidsList) {
            final CopyOnWriteArrayList<Friend> friendConcurrentList = new CopyOnWriteArrayList<>();
            final List<CompletableFuture<Void>> futures = new ArrayList<>(getCapacity(steamids.size()));
            for (String steamid : steamids) {
                futures.add(generateDataFetcherFuture(
                        steamid,
                        (rawFriends, sid) -> {
                            rawFriends.parallelStream().forEach(friend -> friend.setSteamid(sid));
                            return rawFriends;
                            },
                        friendConcurrentList)
                );
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

    @Override
    public int getDatabaseQueryPageSize() {
        return DB_QUERY_PAGE_SIZE;
    }

    @Override
    public int getMaxCountInRound() {
        return MAX_SELECT_STEAMID;
    }

    @Override
    public String getHttpRequestCaption() {
        return GET_FRIEND_LIST_CAPTION;
    }

    @Override
    public int countBasicDataRecords() {
        return playerService.countAllPlayers();
    }

    @Override
    public List<String> findBasicDataByPage(int offset, int pageSize) {
        return playerService.findPlayerSteamIds(offset, pageSize);
    }

    @Override
    public void insertData(Collection<Friend> data) {
        friendService.insertBatch(data);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getSteamWebApiUrl(Object ... params) {
        if (params != null && params.length > 0 && params[0] != null) {
            return steamWebApi.getFriendList((String) params[0]);
        } else {
            throw new RuntimeException("Invalid parameter! ");
        }
    }

    @Override
    public Class<GetFriendListResponse> getResponseClass() {
        return GetFriendListResponse.class;
    }
}