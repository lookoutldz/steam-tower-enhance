package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.domain.App;
import org.demo.steamtowerenhance.dto.steamresponse.AppListEntity;
import org.demo.steamtowerenhance.dto.steamresponse.GetAppListResponse;
import org.demo.steamtowerenhance.mapper.AppMapper;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppFetcher extends AbstractSteamDataFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppFetcher.class);
    private final SteamWebApi steamWebApi;
    private final HttpUtils httpUtils;
    private final AppMapper appMapper;

    public AppFetcher(SteamWebApi steamWebApi, HttpUtils httpUtils, AppMapper appMapper) {
        this.steamWebApi = steamWebApi;
        this.httpUtils = httpUtils;
        this.appMapper = appMapper;
    }

    @Override
    public void fetchApps() {
        int rows = 0;
        GetAppListResponse getAppListResponse = httpUtils.getAsObject(
                steamWebApi.getAppList(),
                "getAppList",
                GetAppListResponse.class,
                null,
                null);
        if (getAppListResponse != null) {
            AppListEntity applist = getAppListResponse.applist();
            if (applist != null) {
                List<App> apps = applist.apps();
                if (apps != null && apps.size() > 0) {
                    rows = apps.size();
                    appMapper.insertBatch(apps);
                }
            }
        }
        LOGGER.info("apps rows: " + rows);
    }
}
