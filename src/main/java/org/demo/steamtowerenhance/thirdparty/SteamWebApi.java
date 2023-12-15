package org.demo.steamtowerenhance.thirdparty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SteamWebApi {

    @Value("${custom.steam.key}")
    private String key;

    // ISteamApps
    public String getAppList() {
        return "https://api.steampowered.com/ISteamApps/GetAppList/v2";
    }

    //ISteamUser
    public String getPlayerSummaries(Collection<String> steamIds) {
        if (steamIds.size() > 100) {
            throw new IllegalArgumentException("steamid 个数超过请求最大限制: 100");
        }
        return "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2?key=" + key + "&steamids=" + String.join(",", steamIds);
    }
    public String getFriendList(String steamId) {
        return "https://api.steampowered.com/ISteamUser/GetFriendList/v1?key=" + key + "&steamid=" + steamId;
    }
    public String resolveVanityUrl(String steamName) {
        return "https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1?key=" + key + "&vanityurl=" + steamName;
    }

    //ISteamUserStats
    public String getGlobalAchievementPercentagesForApp(Integer appId) {
        return "https://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2?key=" + key + "&gameid=" + appId;
    }
    public String getNumberOfCurrentPlayers(Integer appId) {
        return "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1?key=" + key + "&appid=" + appId;
    }
    public String getPlayerAchievementsCN(String steamId, Integer appId) {
        return "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1?key=" + key + "&l=schinese&steamid=" + steamId + "&appid=" + appId;
    }
    public String getPlayerAchievementsEN(String steamId, Integer appId) {
        return "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1?key=" + key + "&steamid=" + steamId + "&appid=" + appId;
    }
    public String getSchemaForGameCN(Integer appId) {
        return "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2?key=" + key + "&l=schinese&appid=" + appId;
    }
    public String getSchemaForGameEN(Integer appId) {
        return "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2?key=" + key + "&appid=" + appId;
    }

    // IPlayerService
    public String getRecentlyPlayedGames(String steamId) {
        return "https://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v1?key=" + key + "&steamid=" + steamId;
    }
    public String getOwnedGames(String steamId) {
        return "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1?key=" + key + "&include_appinfo=1&include_played_free_games=1&steamid=" + steamId;
    }
    public String getSteamLevel(String steamId) {
        return "https://api.steampowered.com/IPlayerService/GetSteamLevel/v1?key=" + key + "&steamid=" + steamId;
    }
}
