package org.demo.steamtowerenhance.databaseoperation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.demo.steamtowerenhance.domain.App;
import org.demo.steamtowerenhance.domain.Player;
import org.demo.steamtowerenhance.dto.steamresponse.GetPlayerSummariesResponse;
import org.demo.steamtowerenhance.job.AppFetcher;
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
    private AppFetcher appFetcher;
    @Autowired
    private PlayerMapper playerMapper;

    @Test
    void getOneApp() {
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("appname");
        queryWrapper.eq("appid", testAppId);
        App app = appMapper.selectOne(queryWrapper);
        System.out.println(app);
    }

    @Test
    void insertApps() {
        long t1 = System.currentTimeMillis();
        appFetcher.fetchApps();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
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
    void refreshPlayerSummaries() {
        long t1 = System.currentTimeMillis();
        // TODO
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void refreshFriendListForPlayer() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        // TODO
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
        Thread.sleep(60000);
    }

    @Test
    void fetchPlayerSummariesForFriendList() {
        long t1 = System.currentTimeMillis();
        // TODO
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }
}
