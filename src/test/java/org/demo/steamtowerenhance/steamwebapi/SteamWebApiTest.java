package org.demo.steamtowerenhance.steamwebapi;

import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class SteamWebApiTest {

    @Value("${custom.steam.test-steam-id}")
    private String testSteamId;

    @Value("${custom.steam.test-app-id}")
    private Integer testAppId;

    @Autowired
    private SteamWebApi steamWebApi;

    @Autowired
    private HttpUtils httpUtils;

    @Test
    void getAppList() throws IOException {
        httpUtils.getToFile(steamWebApi.getAppList(), "getAppList", null);
    }

    @Test
    void getPlayerSummaries() throws IOException {
        httpUtils.getToFile(steamWebApi.getPlayerSummaries(List.of(testSteamId)), "getPlayerSummaries", null);
    }

    @Test
    void getFriendList() throws IOException {
        httpUtils.getToFile(steamWebApi.getFriendList(testSteamId), "getFriendList", null);
    }

    @Test
    void resolveVanityUrl() throws IOException {
        httpUtils.getToFile(steamWebApi.resolveVanityUrl("朝霞"), "resolveVanityUrl", null);
    }

    @Test
    void getGlobalAchievementPercentagesForApp() throws IOException {
        httpUtils.getToFile(steamWebApi.getGlobalAchievementPercentagesForApp(testAppId), "getGlobalAchievementPercentagesForApp", null);
    }

    @Test
    void getNumberOfCurrentPlayers() throws IOException {
        httpUtils.getToFile(steamWebApi.getNumberOfCurrentPlayers(testAppId), "getNumberOfCurrentPlayers", null);
    }

    @Test
    void getPlayerAchievementsCN() throws IOException {
        httpUtils.getToFile(steamWebApi.getPlayerAchievementsCN(testSteamId, testAppId), "getPlayerAchievementsCN", null);
    }

    @Test
    void getPlayerAchievementsEN() throws IOException {
        httpUtils.getToFile(steamWebApi.getPlayerAchievementsEN(testSteamId, testAppId), "getPlayerAchievementsEN", null);
    }

    @Test
    void getSchemaForGameCN() throws IOException {
        httpUtils.getToFile(steamWebApi.getSchemaForGameCN(testAppId), "getSchemaForGameCN", null);
    }

    @Test
    void getSchemaForGameEN() throws IOException {
        httpUtils.getToFile(steamWebApi.getSchemaForGameEN(testAppId), "getSchemaForGameEN", null);
    }

    @Test
    void getRecentlyPlayedGames() throws IOException {
        httpUtils.getToFile(steamWebApi.getRecentlyPlayedGames(testSteamId), "getRecentlyPlayedGames", null);
    }

    @Test
    void getOwnedGames() throws IOException {
        httpUtils.getToFile(steamWebApi.getOwnedGames(testSteamId), "getOwnedGames", null);
    }

    @Test
    void getSteamLevel() throws IOException {
        httpUtils.getToFile(steamWebApi.getSteamLevel(testSteamId), "getSteamLevel", null);
    }
}
