package org.demo.steamtowerenhance.dto.steamresponse;

import org.demo.steamtowerenhance.domain.GameSchema;

import java.io.Serializable;
import java.util.List;

public record AvailableGameStats(List<GameSchema> achievements) implements Serializable {}
