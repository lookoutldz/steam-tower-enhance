package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.Friend;
import org.demo.steamtowerenhance.dto.steamresponse.GetFriendListResponse;
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
public class FriendFetcher extends AbstractBaseOnExistingSteamDataFetcher<Friend, GetFriendListResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendFetcher.class);
    private static final int DB_QUERY_PAGE_SIZE = 1000;
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

    @Override
    public void fetchFriends() {
        fetch();
    }

    @Override
    public List<String> pageableQueryDataAndGenerateUrl(Integer offset, Integer pageSize) {
        return playerService.findPlayerSteamIds(offset, pageSize)
                .parallelStream()
                .map(steamWebApi::getFriendList)
                .toList();
    }

    @Override
    public List<Friend> dataPostProcessor(String url, List<Friend> rawData) {
        //TODO
        return null;
    }

    @Override
    public void insertData(Collection<Friend> data) {
        friendService.insertBatch(data);
    }

    @Override
    public Integer getSelectionPageSize() {
        return DB_QUERY_PAGE_SIZE;
    }

    @Override
    public Class<GetFriendListResponse> getSteamResponseType() {
        return GetFriendListResponse.class;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

}