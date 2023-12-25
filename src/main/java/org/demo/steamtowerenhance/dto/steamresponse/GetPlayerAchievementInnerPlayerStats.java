package org.demo.steamtowerenhance.dto.steamresponse;

import org.demo.steamtowerenhance.domain.PlayerAchievements;

import java.io.Serializable;
import java.util.List;

public record GetPlayerAchievementInnerPlayerStats(
        String steamID,
        String gameName,
        List<PlayerAchievements> achievements,
        Boolean success
) implements Serializable {}
