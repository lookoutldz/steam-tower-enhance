package org.demo.steamtowerenhance.thirdparty;

import org.demo.steamtowerenhance.domain.Player;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SteamWebApiHolder {

    private final SteamWebApi steamWebApi;

    public SteamWebApiHolder(SteamWebApi steamWebApi) {
        this.steamWebApi = steamWebApi;
    }

}
