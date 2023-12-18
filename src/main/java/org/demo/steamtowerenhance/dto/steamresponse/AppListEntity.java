package org.demo.steamtowerenhance.dto.steamresponse;

import org.demo.steamtowerenhance.domain.App;

import java.util.List;

public record AppListEntity(List<App> apps) implements SteamResponse {}