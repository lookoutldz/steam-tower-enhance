package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.PlayerAchievements;
import org.demo.steamtowerenhance.dto.steamresponse.GetPlayerAchievementsResponse;
import org.demo.steamtowerenhance.job.common.AbstractBaseOnExistingSteamDataFetcher;
import org.demo.steamtowerenhance.service.OwnedGameService;
import org.demo.steamtowerenhance.service.PlayerAchievementService;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class PlayerAchievementFetcher extends AbstractBaseOnExistingSteamDataFetcher<PlayerAchievements, GetPlayerAchievementsResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerAchievementFetcher.class);
    private static final int DB_QUERY_PAGE_SIZE = 100;
    private final OwnedGameService ownedGameService;
    private final PlayerAchievementService playerAchievementService;

    public PlayerAchievementFetcher(SteamWebApi steamWebApi,
                                    HttpUtils httpUtils,
                                    CommonThreadPool commonThreadPool,
                                    OwnedGameService ownedGameService,
                                    PlayerAchievementService playerAchievementService) {
        super(steamWebApi, httpUtils, commonThreadPool);
        this.ownedGameService = ownedGameService;
        this.playerAchievementService = playerAchievementService;
    }

    @Override
    public void fetchPlayerAchievements() {
        fetch();
    }

    @Override
    public List<String> pageableQueryDataAndGenerateUrl(Integer offset, Integer pageSize) {
        return ownedGameService.findSteamIdAndAppId(offset, pageSize)
                .parallelStream()
                .map(game -> steamWebApi.getPlayerAchievementsCN(game.getSteamid(), game.getAppid()))
                .toList();
    }

    @Override
    public List<PlayerAchievements> dataPostProcessor(String url, List<PlayerAchievements> rawData) {
        final String steamid = HttpUtils.getUrlParameter(url, "steamid");
        final String appidString = HttpUtils.getUrlParameter(url, "appid");
        if (steamid != null && appidString != null) {
            final Integer appid = Integer.valueOf(appidString);
            rawData.parallelStream().forEach(playerAchievements -> {
                playerAchievements.setSteamid(steamid);
                playerAchievements.setAppid(appid);
            });
        }
        return rawData;
    }

    @Override
    public void insertData(Collection<PlayerAchievements> data) {
        playerAchievementService.insertBatch(data);
    }

    @Override
    public Integer getSelectionPageSize() {
        return DB_QUERY_PAGE_SIZE;
    }

    @Override
    public Class<GetPlayerAchievementsResponse> getSteamResponseType() {
        return GetPlayerAchievementsResponse.class;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
