package org.demo.steamtowerenhance.dto.steamresponse;

public record GetPlayerAchievementsResponse(GetPlayerAchievementInnerPlayerStats playerstats) implements SteamResponse {}
