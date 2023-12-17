package org.demo.steamtowerenhance.job;

import okhttp3.Request;
import okhttp3.Response;
import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.Player;
import org.demo.steamtowerenhance.dto.steamresponse.GetPlayerSummariesResponse;
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
public class PlayerFetcher extends AbstractSteamDataFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerFetcher.class);

    private static final int REQUEST_ERROR_CODE = 999;
    private static final int FUTURE_ERROR_CODE = 998;
    private static final String GET_PLAYER_CAPTION = "getPlayerSummaries";
    private static final String REQUEST_PARAM_STEAMID = "steamids";
    private static final int MAX_UPDATE_STEAMID = 1000;
    private static final int DB_QUERY_PAGE_SIZE = 10000;
    private final ConcurrentHashMap<String, Integer> failedMap = new ConcurrentHashMap<>();

    private final SteamWebApi steamWebApi;
    private final HttpUtils httpUtils;
    private final PlayerService playerService;
    private final FriendService friendService;
    private final CommonThreadPool commonThreadPool;

    public PlayerFetcher(SteamWebApi steamWebApi, HttpUtils httpUtils, PlayerService playerService, FriendService friendService, CommonThreadPool commonThreadPool) {
        this.steamWebApi = steamWebApi;
        this.httpUtils = httpUtils;
        this.playerService = playerService;
        this.friendService = friendService;
        this.commonThreadPool = commonThreadPool;
    }

    @Override
    public void fetchPlayers() {
        fetchPlayersForFriends();
    }

    private void fetchPlayersForFriends() {
        int total = 0;
        // TODO 1. count the number of friend
        Integer allFriendCount = friendService.countByDistinctFriendsteamid();

        // TODO 2. select steamid of friends partly if necessary
        final int pageSize = DB_QUERY_PAGE_SIZE;
        final int pageCount = allFriendCount > pageSize ? allFriendCount / pageSize + 1 : 1;

        // TODO 2. fetch api async
        for (int i = 0; i < pageCount; i++) {
            int rows = 0;
            List<String> pagedSteamIds = friendService.findDistinctFriendSteamIds(i * pageSize, pageSize);
            List<List<String>> steamidsList = separateList(pagedSteamIds, MAX_UPDATE_STEAMID);
            final int apiMaxSteamIdCount = 100;
            for (int j = 0; j < steamidsList.size(); j++) {
                final List<String> steamids = steamidsList.get(j);
                final int capacity = getCapacity(steamids.size());
                final CopyOnWriteArrayList<Player> playerConcurrentList = new CopyOnWriteArrayList<>();
                final List<CompletableFuture<Void>> futures = new ArrayList<>(capacity);
                final List<List<String>> resizedSteamIdsList =
                        steamids.size() > apiMaxSteamIdCount
                                ? separateList(steamids, apiMaxSteamIdCount)
                                : List.of(steamids);

                for (List<String> resizedSteamIds : resizedSteamIdsList) {
                    futures.add(generateFutureTask(resizedSteamIds, playerConcurrentList));
                }

                CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

                try {
                    // 同步
                    allOfFuture.get();
                    final int row = playerConcurrentList.size();
                    if (row > 0) {
                        playerService.insertBatch(playerConcurrentList);
                    }
                    LOGGER.debug("Group " + j + " finished, rows = " + row);
                    rows += row;
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Error while future getting: " + e.getMessage(), e);
                    for (String steamid : steamids) {
                        failedMap.put(steamid, FUTURE_ERROR_CODE);
                    }
                }
            }
            LOGGER.info("本轮次: " + i +", 查询 friendsteamid: " + pagedSteamIds.size() + ", DB 有效 player 记录: " + rows);
            total += rows;
        }
        LOGGER.info("本次 fetch 总数据, total: " + total);
    }

    public CompletableFuture<Void> generateFutureTask(Collection<String> steamids, Collection<Player> playerConcurrentList) {
        return CompletableFuture
                .supplyAsync(() ->
                        httpUtils.getAsObject(
                                steamWebApi.getPlayerSummaries(steamids),
                                GET_PLAYER_CAPTION,
                                GetPlayerSummariesResponse.class,
                                this::requestFailedHandler,
                                this::requestErrorHandler
                        ), commonThreadPool)
                .thenAcceptAsync(res -> {
                    final List<Player> players;
                    if (res != null
                            && res.response() != null
                            && (players = res.response().players()) != null && !players.isEmpty()) {
                        playerConcurrentList.addAll(players);
                    }
                })
                .handleAsync((unused, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error("Something went wrong: " + throwable.getMessage(), throwable);
                    }
                    return unused;
                });
    }

    private GetPlayerSummariesResponse requestFailedHandler(Response response) {
        String steamidListString = response.request().url().queryParameter(REQUEST_PARAM_STEAMID);
        final int code = response.code();
        if (steamidListString != null) {
            String[] steamids = steamidListString.split(",");
            for (String steamid : steamids) {
                if (steamid.length() > 0) {
                    failedMap.put(steamid, code);
                }
            }
        } else {
            LOGGER.warn("HTTP " + code + " for null steamid");
        }
        return null;
    }

    private void requestErrorHandler(Request request, Throwable throwable) {
        String steamidListString = request.url().queryParameter(REQUEST_PARAM_STEAMID);
        if (steamidListString != null) {
            String[] steamids = steamidListString.split(",");
            for (String steamid : steamids) {
                if (steamid.length() > 0) {
                    failedMap.put(steamid, REQUEST_ERROR_CODE);
                }
            }
        } else {
            if (throwable != null) {
                LOGGER.error("Request error: " + throwable.getMessage(), throwable);
            }
        }
    }

    public void reFetchFailedRecords() {
        final ConcurrentHashMap.KeySetView<String, Integer> keys = failedMap.keySet();
        final int size = keys.size();
        LOGGER.info("failedMap size : " + size);
        if (size < 1) {
            return;
        }
        final int apiMaxSteamIdCount = 100;

        // 拆分
        final List<List<String>> steamidsList =
                size > MAX_UPDATE_STEAMID
                        ? separateList(keys.stream().toList(), MAX_UPDATE_STEAMID)
                        : List.of(keys.stream().toList());

        for (List<String> steamids : steamidsList) {
            final int capacity = getCapacity(steamids.size());
            final CopyOnWriteArrayList<Player> playerConcurrentList = new CopyOnWriteArrayList<>();
            final List<CompletableFuture<Void>> futures = new ArrayList<>(capacity);

            final List<List<String>> resizedSteamIdsList =
                    steamids.size() > apiMaxSteamIdCount
                            ? separateList(steamids, apiMaxSteamIdCount)
                            : List.of(steamids);

            for (List<String> resizedSteamIds: resizedSteamIdsList) {
                futures.add(generateFutureTask(resizedSteamIds, playerConcurrentList));
            }
            CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            try {
                allOfFuture.get();
                final int row = playerConcurrentList.size();
                if (row > 0) {
                    playerService.insertBatch(playerConcurrentList);
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
