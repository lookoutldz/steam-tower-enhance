package org.demo.steamtowerenhance.dto.steamresponse;

import org.demo.steamtowerenhance.domain.Friend;

import java.util.List;

public record FriendListEntity(List<Friend> friends) implements SteamResponse {}