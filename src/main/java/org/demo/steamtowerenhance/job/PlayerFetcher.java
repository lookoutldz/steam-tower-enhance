package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.Player;
import org.demo.steamtowerenhance.dto.steamresponse.GetPlayerSummariesResponse;
import org.demo.steamtowerenhance.job.common.AbstractBaseOnExistingSteamDataFetcher;
import org.demo.steamtowerenhance.service.FriendService;
import org.demo.steamtowerenhance.service.PlayerService;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class PlayerFetcher extends AbstractBaseOnExistingSteamDataFetcher<Player, GetPlayerSummariesResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerFetcher.class);
    private static final int DB_QUERY_PAGE_SIZE = 1000;
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
    public void fetchPlayers() {
        fetch();
    }

    @Override
    public List<String> pageableQueryDataAndGenerateUrl(Integer offset, Integer pageSize) {
        final List<String> distinctFriendSteamIds = friendService.findDistinctFriendSteamIds(offset, pageSize);
        final int apiCountLimit = 100;
        final List<List<String>> steamids =
                distinctFriendSteamIds.size() > apiCountLimit
                        ? separateList(distinctFriendSteamIds, apiCountLimit)
                        : List.of(distinctFriendSteamIds);
        return steamids
                .parallelStream()
                .map(steamWebApi::getPlayerSummaries)
                .toList();
    }

    @Override
    public void insertData(Collection<Player> data) {
        playerService.insertBatch(data);
    }

    @Override
    public Integer getSelectionPageSize() {
        return DB_QUERY_PAGE_SIZE;
    }

    @Override
    public Class<GetPlayerSummariesResponse> getSteamResponseType() {
        return GetPlayerSummariesResponse.class;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}