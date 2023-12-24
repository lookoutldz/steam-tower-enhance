package org.demo.steamtowerenhance.job.common;

public interface SteamDataFetcher {
    void fetchApps();

    void fetchPlayers();

    void fetchFriends();

    void fetchOwnedGames();

    void fetchGameSchemas();

    void fetchPlayerAchievements();
}