package org.demo.steamtowerenhance.controller;

import org.demo.steamtowerenhance.job.FriendFetcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/steamdata")
public class SteamDataController {

    private final FriendFetcher friendFetcher;

    public SteamDataController(FriendFetcher friendFetcher) {
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

}
