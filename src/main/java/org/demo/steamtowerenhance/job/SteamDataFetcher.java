package org.demo.steamtowerenhance.job;

interface SteamDataFetcher {
    void fetchApps();

    void fetchPlayers();

    void fetchFriends();

    void fetchOwnedGames();

    void fetchGameSchemas();

    void fetchPlayerAchievements();
}