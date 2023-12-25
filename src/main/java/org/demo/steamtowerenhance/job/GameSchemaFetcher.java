package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.GameSchema;
import org.demo.steamtowerenhance.dto.steamresponse.GetSchemaForGameResponse;
import org.demo.steamtowerenhance.job.common.AbstractBaseOnExistingSteamDataFetcher;
import org.demo.steamtowerenhance.service.AppService;
import org.demo.steamtowerenhance.service.GameSchemaService;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class GameSchemaFetcher extends AbstractBaseOnExistingSteamDataFetcher<GameSchema, GetSchemaForGameResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSchemaFetcher.class);
    private static final int DB_QUERY_PAGE_SIZE = 100;
    private final AppService appService;
    private final GameSchemaService gameSchemaService;

    public GameSchemaFetcher(SteamWebApi steamWebApi,
                             HttpUtils httpUtils,
                             CommonThreadPool commonThreadPool,
                             AppService appService,
                             GameSchemaService gameSchemaService) {
        super(steamWebApi, httpUtils, commonThreadPool);
        this.appService = appService;
        this.gameSchemaService = gameSchemaService;
    }

    @Override
    public void fetchGameSchemas() {
        fetch();
    }

    @Override
    public List<String> pageableQueryDataAndGenerateUrl(Integer offset, Integer pageSize) {
        return appService.findAppIds(offset, pageSize)
                .parallelStream()
                .map(steamWebApi::getSchemaForGameCN)
                .toList();
    }

    @Override
    public List<GameSchema> dataPostProcessor(String url, List<GameSchema> rawData) {
        String appidString = HttpUtils.getUrlParameter(url, "appid");
        if (appidString != null) {
            Integer appid = Integer.valueOf(appidString);
            rawData.parallelStream().forEach(data -> data.setAppid(appid));
        }
        return rawData;
    }

    @Override
    public void insertData(Collection<GameSchema> data) {
        gameSchemaService.insertBatch(data);
    }

    @Override
    public Integer getSelectionPageSize() {
        return DB_QUERY_PAGE_SIZE;
    }

    @Override
    public Class<GetSchemaForGameResponse> getSteamResponseType() {
        return GetSchemaForGameResponse.class;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
