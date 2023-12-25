package org.demo.steamtowerenhance.dto.steamresponse;

import org.demo.steamtowerenhance.domain.OwnedGame;

import java.util.List;

public record GetOwnedGamesResponse(GetOwnedGamesInnerResponse response) implements SteamResponse {}
