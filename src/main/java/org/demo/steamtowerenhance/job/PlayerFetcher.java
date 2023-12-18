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
public class PlayerFetcher extends AbstractBaseOnExistingSteamDataFetcher<String, List<String>, Player, GetPlayerSummariesResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerFetcher.class);

    private static final int REQUEST_ERROR_CODE = 999;
    private static final int FUTURE_ERROR_CODE = 998;
    private static final String GET_PLAYER_CAPTION = "getPlayerSummaries";
    private static final String REQUEST_PARAM_STEAMID = "steamids";
    private static final int MAX_SELECT_STEAMID = 1000;
    private static final int DB_QUERY_PAGE_SIZE = 10000;
    private final ConcurrentHashMap<String, Integer> failedMap = new ConcurrentHashMap<>();

    private final PlayerService playerService;
    private final FriendService friendService;

    public PlayerFetcher(SteamWebApi steamWebApi,
                         HttpUtils httpUtils,
                         CommonThreadPool commonThreadPool,
                         PlayerService playerService,
                         FriendService friendService) {
        super(steamWebApi, httpUtils, commonThreadPool);
        this.playerService = playerService;
        this.friendService = friendService;
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
        return GET_PLAYER_CAPTION;
    }

    @Override
    public int countBasicDataRecords() {
        return friendService.countByDistinctFriendsteamid();
    }

    @Override
    public List<String> findBasicDataByPage(int offset, int pageSize) {
        return friendService.findDistinctFriendSteamIds(offset, pageSize);
    }

    @Override
    public void insertData(Collection<Player> data) {
        playerService.insertBatch(data);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getSteamWebApiUrl(Object... params) {
        return null;
    }

    @Override
    public Class<GetPlayerSummariesResponse> getResponseClass() {
        return GetPlayerSummariesResponse.class;
    }

    @Override
    public void fetchPlayers() {
        fetchPlayersForFriends();
    }

    private void fetchPlayersForFriends() {

        final int apiMaxSteamIdCount = 100;
        process(
                steamids -> steamids.size() > apiMaxSteamIdCount
                        ? separateList(steamids, apiMaxSteamIdCount)
                        : List.of(steamids),
                null,
                (throwable, steamidsList) -> {
                    getLogger().error("Failed to fetch players.", throwable);
                    for (List<String> steamids : steamidsList) {
                        for (String steamid : steamids) {
                            failedMap.put(steamid, FUTURE_ERROR_CODE);
                        }
                    }
                }
        );
    }

    public GetPlayerSummariesResponse requestFailedHandler(Response response) {
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

    public void requestErrorHandler(Request request, Throwable throwable) {
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
                size > MAX_SELECT_STEAMID
                        ? separateList(keys.stream().toList(), MAX_SELECT_STEAMID)
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
                futures.add(
                        generateDataFetcherFuture(resizedSteamIds, null, playerConcurrentList)
                );
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