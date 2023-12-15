package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.Friend;
import org.demo.steamtowerenhance.domain.Player;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

@Deprecated
@Component
public class SteamDataFetchJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SteamDataFetchJob.class);

    private final SteamWebApi steamWebApi;
    private final AppMapper appMapper;
    private final PlayerMapper playerMapper;
    private final FriendMapper friendMapper;
    private final HttpUtils httpUtils;
    private final CommonThreadPool commonThreadPool;

    public SteamDataFetchJob(SteamWebApi steamWebApi,
                             AppMapper appMapper,
                             PlayerMapper playerMapper,
                             FriendMapper friendMapper,
                             HttpUtils httpUtils, CommonThreadPool commonThreadPool, CommonThreadPool commonThreadPool1) {
        this.steamWebApi = steamWebApi;
        this.appMapper = appMapper;
        this.playerMapper = playerMapper;
        this.friendMapper = friendMapper;
        this.httpUtils = httpUtils;
        this.commonThreadPool = commonThreadPool1;
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
            // 请求
            final GetPlayerSummariesResponse playerSummariesFuture = httpUtils.getAsObject(
                    steamWebApi.getPlayerSummaries(group), "GetPlayerSummaries", GetPlayerSummariesResponse.class, null, null);

            final List<Player> players;
            final GetPlayerSummariesResponse playerSummariesResponse;
            if ((playerSummariesResponse = playerSummariesFuture) != null
                    && playerSummariesResponse.response() != null
                    && (players = playerSummariesResponse.response().players()) != null) {
                fullPlayers.addAll(players);
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

//    @Deprecated
    public void refreshFriendListForPlayer0() {
        int rows = 0;
        // 获取 Player
        final List<String> steamids = playerMapper.findAllPlayerSteamIds();
        // 为避免内存溢出, 每次更新 friend 表不超过 1 万, 按平均一个用户 100 个好友估算, 1 万数据约为 100 个 player 的量
        List<List<String>> groups = separateList(steamids, 100);
        LOGGER.info("Steamids size: " + steamids.size() + ", separated to " + groups.size() + " groups");

        for (int i = 0; i < groups.size(); i++) {
            final List<String> group = groups.get(i);
            // 按 100 好友估算 capacity
            final int capacity = getCapacity((steamids.size() * 100));
            final Map<String, GetFriendListResponse> futures = new HashMap<>(capacity);
            // 获取每个 Player 的 Friend
            for (String steamid : group) {
                GetFriendListResponse friendListResponse = httpUtils.getAsObject(
                        steamWebApi.getFriendList(steamid), "getFriendList", GetFriendListResponse.class, null , null);
                futures.put(steamid, friendListResponse);
            }
            // 合并本组内的数据并进行一次数据库写入
            final List<Friend> fullFriendList = new ArrayList<>(capacity);
            for (Map.Entry<String, GetFriendListResponse> entry : futures.entrySet()) {
                final String steamid = entry.getKey();
                final GetFriendListResponse future = entry.getValue();
                final List<Friend> friends;
                final GetFriendListResponse getFriendListResponse;
                if ((getFriendListResponse = future) != null
                        && getFriendListResponse.friendslist() != null
                        && (friends = getFriendListResponse.friendslist().friends()) != null) {
                    for (Friend friend : friends) {
                        friend.setSteamid(steamid);
                    }
                    if (friends.size() > 0) {
                        fullFriendList.addAll(friends);
                    } else {
                        LOGGER.warn("friends list for " + steamid + " is empty");
                    }
                } else {
                        LOGGER.warn("friends list for " + steamid + " is null result");
                }
            }
            if (fullFriendList.isEmpty()) {
                LOGGER.warn("Group " + i + " is empty.");
                continue;
            }
            friendMapper.insertBatch(fullFriendList);
            rows += fullFriendList.size();
            LOGGER.info("Group " + i + " finished.");
        }
        LOGGER.info("All friends rows: " + rows);
    }

    public void refreshFriendListForPlayer() {
        int rows = 0;
        // 为避免内存溢出, 每次更新 friend 表不超过 1 万, 按平均一个用户 100 个好友估算, 1 万数据约为 100 个 player 的量
        final List<String> steamids = playerMapper.findAllPlayerSteamIds();
        final List<List<String>> groups = separateList(steamids, 100);
        LOGGER.info("Steamids size: " + steamids.size() + ", separated to " + groups.size() + " groups");

        for (int i = 0; i < groups.size(); i++) {
            final List<String> group = groups.get(i);
            final List<CompletableFuture<List<Friend>>> completableFutures = group.parallelStream()
                    .map(steamid -> CompletableFuture.supplyAsync(() -> httpUtils.getAsObject(steamWebApi.getFriendList(steamid), "getFriendList", GetFriendListResponse.class, null, null))
                            .thenApplyAsync(response -> processFriendListResponse(response, steamid), commonThreadPool))
                    .toList();

            CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));

            try {
                allOf.get(); // 等待所有异步任务完成
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error while getting friends.", e);
            }

            List<Friend> fullFriendList = completableFutures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .collect(toList());

            friendMapper.insertBatch(fullFriendList);
            rows += fullFriendList.size();
            LOGGER.info("Group " + i + " finished.");
        }

        LOGGER.info("All friends rows: " + rows);
    }
    private List<Friend> processFriendListResponse(GetFriendListResponse response, String steamid) {
        if (response != null && response.friendslist() != null) {
            return response.friendslist().friends().stream()
                    .peek(friend -> friend.setSteamid(steamid))
                    .collect(toList());
        }
        return Collections.emptyList();
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
        final List<GetPlayerSummariesResponse> futures = new ArrayList<>(capacity);

        for (List<String> group : groups) {
            final GetPlayerSummariesResponse playerSummariesFuture = httpUtils.getAsObject(
                    steamWebApi.getPlayerSummaries(group), "GetPlayerSummaries", GetPlayerSummariesResponse.class, null, null);
            futures.add(playerSummariesFuture);
        }
        for (GetPlayerSummariesResponse future : futures) {
            final GetPlayerSummariesResponse getPlayerSummariesResponse;
            final List<Player> players;
            if ((getPlayerSummariesResponse = future) != null
                    && getPlayerSummariesResponse.response() != null
                    && (players = getPlayerSummariesResponse.response().players()) != null) {
                fullPlayers.addAll(players);
            }
        }

        // 更新 DB
        playerMapper.insertBatch(fullPlayers);
        LOGGER.info("players rows: " + fullPlayers.size());
    }
}
