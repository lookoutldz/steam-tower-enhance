package org.demo.steamtowerenhance.databaseoperation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.demo.steamtowerenhance.domain.App;
import org.demo.steamtowerenhance.domain.Player;
import org.demo.steamtowerenhance.dto.steamresponse.GetPlayerSummariesResponse;
import org.demo.steamtowerenhance.job.*;
import org.demo.steamtowerenhance.mapper.AppMapper;
import org.demo.steamtowerenhance.mapper.PlayerMapper;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DatabaseOperationTest {

    @Value("${custom.steam.test-steam-id}")
    private String testSteamId;
    @Value("${custom.steam.test-app-id}")
    private Integer testAppId;

    @Autowired
    private SteamWebApi steamWebApi;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private AppMapper appMapper;
    @Autowired
    private PlayerMapper playerMapper;
    @Autowired
    private AppFetcher appFetcher;
    @Autowired
    private FriendFetcher friendFetcher;
    @Autowired
    private PlayerFetcher playerFetcher;
    @Autowired
    private OwnedGameFetcher ownedGameFetcher;
    @Autowired
    private GameSchemaFetcher gameSchemaFetcher;
    @Autowired
    private PlayerAchievementFetcher playerAchievementFetcher;

    @Test
    void getOneApp() {
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("appname");
        queryWrapper.eq("appid", testAppId);
        App app = appMapper.selectOne(queryWrapper);
        System.out.println(app);
    }

    @Test
    void insertPlayer() {
        long t1 = System.currentTimeMillis();
        GetPlayerSummariesResponse res = httpUtils.getAsObject(
                steamWebApi.getPlayerSummaries(List.of(testSteamId)),
                "testInsertPlayer",
                GetPlayerSummariesResponse.class, null, null);
        final Player player;
        if (res != null
                && res.response() != null
                && res.response().players() != null && !res.response().players().isEmpty()
                && (player = res.response().players().get(0)) != null) {
            System.out.println(playerMapper.insert(player));
        }
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void fetchApps() {
        long t1 = System.currentTimeMillis();
        appFetcher.fetchApps();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void fetchPlayers() {
        long t1 = System.currentTimeMillis();
        playerFetcher.fetchPlayers();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void fetchFriends() {
        long t1 = System.currentTimeMillis();
        friendFetcher.fetchFriends();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void fetchOwnedGames() {
        long t1 = System.currentTimeMillis();
        ownedGameFetcher.fetchOwnedGames();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void fetchGameSchemas() {
        long t1 = System.currentTimeMillis();
        gameSchemaFetcher.fetchGameSchemas();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }


    @Test
    void fetchPlayerAchievements() {
        long t1 = System.currentTimeMillis();
        playerAchievementFetcher.fetchPlayerAchievements();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

}
