package org.demo.steamtowerenhance.dto.steamresponse;

import java.io.Serializable;

public record GetSchemaForGameInnerGame(String gameName, String gameVersion, AvailableGameStats availableGameStats) implements Serializable {}
