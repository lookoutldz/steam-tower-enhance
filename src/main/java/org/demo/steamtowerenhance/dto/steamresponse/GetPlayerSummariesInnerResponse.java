package org.demo.steamtowerenhance.dto.steamresponse;

import org.demo.steamtowerenhance.domain.Player;

import java.util.List;

public record GetPlayerSummariesInnerResponse(List<Player> players) implements SteamResponse {}