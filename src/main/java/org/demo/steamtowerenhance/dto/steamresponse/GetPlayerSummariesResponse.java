package org.demo.steamtowerenhance.dto.steamresponse;

public record GetPlayerSummariesResponse(GetPlayerSummariesInnerResponse response) implements SteamResponse {}