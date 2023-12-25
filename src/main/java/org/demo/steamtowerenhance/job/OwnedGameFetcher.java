package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.OwnedGame;
import org.demo.steamtowerenhance.dto.steamresponse.GetOwnedGamesResponse;
import org.demo.steamtowerenhance.job.common.AbstractBaseOnExistingSteamDataFetcher;
import org.demo.steamtowerenhance.service.OwnedGameService;
import org.demo.steamtowerenhance.service.PlayerService;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class OwnedGameFetcher extends AbstractBaseOnExistingSteamDataFetcher<OwnedGame, GetOwnedGamesResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnedGameFetcher.class);
    private static final int DB_QUERY_PAGE_SIZE = 100;
    private final PlayerService playerService;
    private final OwnedGameService ownedGameService;

    public OwnedGameFetcher(SteamWebApi steamWebApi,
                            HttpUtils httpUtils,
                            CommonThreadPool commonThreadPool,
                            PlayerService playerService,
                            OwnedGameService ownedGameService) {
        super(steamWebApi, httpUtils, commonThreadPool);
        this.playerService = playerService;
        this.ownedGameService = ownedGameService;
    }

    @Override
    public void fetchOwnedGames() {
        fetch();
    }

    @Override
    public List<String> pageableQueryDataAndGenerateUrl(Integer offset, Integer pageSize) {
        return playerService.findPlayerSteamIds(offset, pageSize)
                .parallelStream()
                .map(steamWebApi::getOwnedGames)
                .toList();
    }

    @Override
    public List<OwnedGame> dataPostProcessor(String url, List<OwnedGame> rawData) {
        String steamid = HttpUtils.getUrlParameter(url, "steamid");
        if (steamid == null) {
            return rawData;
        }
        rawData.parallelStream().forEach(game -> game.setSteamid(steamid));
        return rawData;
    }

    @Override
    public void insertData(Collection<OwnedGame> data) {
        ownedGameService.insertBatch(data);
    }

    @Override
    public Integer getSelectionPageSize() {
        return DB_QUERY_PAGE_SIZE;
    }

    @Override
    public Class<GetOwnedGamesResponse> getSteamResponseType() {
        return GetOwnedGamesResponse.class;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
