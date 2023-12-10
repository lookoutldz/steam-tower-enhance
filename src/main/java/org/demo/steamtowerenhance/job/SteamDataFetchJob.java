package org.demo.steamtowerenhance.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.App;
import org.demo.steamtowerenhance.domain.Friend;
import org.demo.steamtowerenhance.domain.Player;
import org.demo.steamtowerenhance.dto.steamresponse.AppListEntity;
import org.demo.steamtowerenhance.dto.steamresponse.GetAppListResponse;
import org.demo.steamtowerenhance.dto.steamresponse.GetFriendListResponse;
import org.demo.steamtowerenhance.dto.steamresponse.GetPlayerSummariesResponse;
import org.demo.steamtowerenhance.mapper.AppMapper;
import org.demo.steamtowerenhance.mapper.FriendMapper;
import org.demo.steamtowerenhance.mapper.PlayerMapper;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class SteamDataFetchJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SteamDataFetchJob.class);

    private final SteamWebApi steamWebApi;
    private final AppMapper appMapper;
    private final PlayerMapper playerMapper;
    private final FriendMapper friendMapper;
    private final HttpUtils httpUtils;

    public SteamDataFetchJob(SteamWebApi steamWebApi,
                             AppMapper appMapper,
                             PlayerMapper playerMapper,
                             FriendMapper friendMapper,
                             HttpUtils httpUtils, CommonThreadPool commonThreadPool) {
        this.steamWebApi = steamWebApi;
        this.appMapper = appMapper;
        this.playerMapper = playerMapper;
        this.friendMapper = friendMapper;
        this.httpUtils = httpUtils;
    }

    public void refreshAppList() {
        int rows = 0;
        GetAppListResponse getAppListResponse = httpUtils.getAsObject(steamWebApi.getAppList(), "getAppList", GetAppListResponse.class);
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

    // 将 list 按给定 size 分组
    private<T> List<List<T>> separateList(List<T> list, int groupSize) {
        final List<List<T>> groups;
        if (list.size() > groupSize) {
            final int groupCount = list.size() / groupSize + 1;
            groups = new ArrayList<>(getCapacity(groupCount));
            for (int i = 0, fromIndex, toIndex; i < groupCount; i++) {
                fromIndex = i * groupSize;
                toIndex = Math.min(list.size(), (i + 1) * groupSize);
                groups.add(list.subList(fromIndex, toIndex));
            }
        } else {
            groups = List.of(list);
        }
        return groups;
    }

    private int getCapacity(int actual) {
        return (int) (actual / 0.75) + 1;
    }

    private int refreshPlayerSummariesByParts(List<String> steamids) {
        // 为避免内存溢出, 每次更新 player 表不超过 1 万条
        if (steamids.size() > 10000) {
            throw new IllegalArgumentException("请将 steamid 的数量限制在不大于 10000!");
        }
        // 每组最多 100 个 steamid, 因为此 api 一次性最多请求 100 个 steamid
        final int apiMax = 100;
        final List<List<String>> groups = separateList(steamids, apiMax);
        List<Player> fullPlayers = new ArrayList<>(getCapacity(groups.size() * apiMax));
        for (List<String> group : groups) {
            // 异步请求
            final Future<GetPlayerSummariesResponse> playerSummariesFuture = httpUtils.getAsObjectAsync(
                    steamWebApi.getPlayerSummaries(group), "GetPlayerSummaries", GetPlayerSummariesResponse.class);

            final List<Player> players;
            final GetPlayerSummariesResponse playerSummariesResponse;
            try {
                if ((playerSummariesResponse = playerSummariesFuture.get()) != null
                        && playerSummariesResponse.response() != null
                        && (players = playerSummariesResponse.response().players()) != null) {
                    fullPlayers.addAll(players);
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error while getting part of players.", e);
            }
        }
        // 合并后插入数据库
        playerMapper.insertBatch(fullPlayers);
        return fullPlayers.size();
    }

    public void refreshPlayerSummaries() {
        int rows = 0;
        final List<String> steamids = playerMapper.findAllPlayerSteamIds();
        // 分批次处理
        final List<List<String>> groups = separateList(steamids, 10000);
        for (List<String> group : groups) {
            rows += refreshPlayerSummariesByParts(group);
        }
        LOGGER.info("total players rows: " + rows);
    }

    public void refreshFriendListForPlayer() {
        int rows = 0;
        // 获取 Player
        final List<String> steamids = playerMapper.findAllPlayerSteamIds();
        // 为避免内存溢出, 每次更新 friend 表不超过 10 万, 按平均一个用户 100 个好友估算, 10 万数据约为 1000 个 player 的量
        List<List<String>> groups = separateList(steamids, 1000);
        for (List<String> group : groups) {
            // 按 100 好友估算 capacity
            final int capacity = getCapacity((steamids.size() * 100));
            final Map<String, Future<GetFriendListResponse>> futures = new HashMap<>(capacity);
            // 获取每个 Player 的 Friend
            for (String steamid : group) {
                Future<GetFriendListResponse> friendListResponseFuture = httpUtils.getAsObjectAsync(
                        steamWebApi.getFriendList(steamid), "getFriendList", GetFriendListResponse.class);
                futures.put(steamid, friendListResponseFuture);
            }
            // 合并本组内的数据并进行一次数据库写入
            final List<Friend> fullFriendList = new ArrayList<>(capacity);
            try {
                for (Map.Entry<String, Future<GetFriendListResponse>> entry : futures.entrySet()) {
                    final String steamid = entry.getKey();
                    final Future<GetFriendListResponse> future = entry.getValue();
                    final List<Friend> friends;
                    final GetFriendListResponse getFriendListResponse;
                    if ((getFriendListResponse = future.get()) != null
                            && getFriendListResponse.friendslist() != null
                            && (friends = getFriendListResponse.friendslist().friends()) != null) {
                        for (Friend friend : friends) {
                            friend.setSteamid(steamid);
                        }
                        fullFriendList.addAll(friends);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error while getting part of friends.", e);
            }
            friendMapper.insertBatch(fullFriendList);
            rows += fullFriendList.size();
        }
        LOGGER.info("All friends rows: " + rows);
    }

    public void fetchPlayerSummariesForFriendList() {
        // 分批预处理
        final List<List<String>> groups;
        final List<String> steamids = friendMapper.selectAllDistinctFriendsId();
        LOGGER.info("friendsteamids size: " + steamids.size());

        if (steamids.size() > 100) {
            final int groupCount = steamids.size() / 100 + 1;
            groups = new ArrayList<>(getCapacity(groupCount));
            for (int i = 0; i < groupCount; i++) {
                final int fromIndex = i * 100;
                final int toIndex = Math.min((i + 1) * 100, steamids.size());
                groups.add(steamids.subList(fromIndex, toIndex));
            }
        } else {
            groups = List.of(steamids);
        }

        // 多线程请求
        final int capacity = getCapacity(steamids.size() * 100);
        final List<Player> fullPlayers = new ArrayList<>(capacity);
        final List<Future<GetPlayerSummariesResponse>> futures = new ArrayList<>(capacity);

        for (List<String> group : groups) {
            final Future<GetPlayerSummariesResponse> playerSummariesFuture = httpUtils.getAsObjectAsync(
                    steamWebApi.getPlayerSummaries(group), "GetPlayerSummaries", GetPlayerSummariesResponse.class);
            futures.add(playerSummariesFuture);
        }
        try {
            for (Future<GetPlayerSummariesResponse> future : futures) {
                final GetPlayerSummariesResponse getPlayerSummariesResponse;
                final List<Player> players;
                if ((getPlayerSummariesResponse = future.get()) != null
                        && getPlayerSummariesResponse.response() != null
                        && (players = getPlayerSummariesResponse.response().players()) != null) {
                    fullPlayers.addAll(players);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // 更新 DB
        playerMapper.insertBatch(fullPlayers);
        LOGGER.info("players rows: " + fullPlayers.size());
    }
}
