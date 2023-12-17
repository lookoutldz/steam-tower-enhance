package org.demo.steamtowerenhance.controller;

import org.demo.steamtowerenhance.job.FriendFetcher;
import org.demo.steamtowerenhance.job.PlayerFetcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/steamdata")
public class SteamDataController {

    private final PlayerFetcher playerFetcher;
    private final FriendFetcher friendFetcher;

    public SteamDataController(PlayerFetcher playerFetcher, FriendFetcher friendFetcher) {
        this.playerFetcher = playerFetcher;
        this.friendFetcher = friendFetcher;
    }

    @GetMapping("/friends")
    public Object fetchFriendsByPlayer() {
        friendFetcher.fetchFriends();
        return "finished";
    }

    @GetMapping("/friends/failed")
    public Object reFetchFriendsFailed() {
        friendFetcher.reFetchFailedRecords();
        return "finished";
    }

    @GetMapping("/players")
    public Object fetchPlayersByFriends() {
        playerFetcher.fetchPlayers();
        return "finished";
    }

    @GetMapping("/players/failed")
    public Object reFetchPlayersByFriends() {
        playerFetcher.reFetchFailedRecords();
        return "finished";
    }

}
