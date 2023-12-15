package org.demo.steamtowerenhance.databaseoperation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.demo.steamtowerenhance.domain.App;
import org.demo.steamtowerenhance.job.SteamDataFetchJob;
import org.demo.steamtowerenhance.mapper.AppMapper;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DatabaseOperationTest {

    @Value("${custom.test-app-id}")
    private Integer testAppId;

    @Autowired
    private SteamWebApi steamWebApi;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private AppMapper appMapper;
    @Autowired
    private SteamDataFetchJob steamDataFetchJob;

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
        steamDataFetchJob.refreshAppList();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void refreshPlayerSummaries() {
        long t1 = System.currentTimeMillis();
        steamDataFetchJob.refreshPlayerSummaries();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }

    @Test
    void refreshFriendListForPlayer() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        steamDataFetchJob.refreshFriendListForPlayer();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
        Thread.sleep(60000);
    }

    @Test
    void fetchPlayerSummariesForFriendList() {
        long t1 = System.currentTimeMillis();
        steamDataFetchJob.fetchPlayerSummariesForFriendList();
        long t2 = System.currentTimeMillis();
        System.out.println("by " + (t2 - t1) + "ms");
    }
}
