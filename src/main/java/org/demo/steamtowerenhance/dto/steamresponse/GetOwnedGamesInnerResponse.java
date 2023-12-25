package org.demo.steamtowerenhance.dto.steamresponse;

import org.demo.steamtowerenhance.domain.OwnedGame;

import java.io.Serializable;
import java.util.List;

public record GetOwnedGamesInnerResponse(Integer game_count, List<OwnedGame> games) implements Serializable {}
