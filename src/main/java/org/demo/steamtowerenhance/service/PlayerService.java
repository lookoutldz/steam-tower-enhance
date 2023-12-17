package org.demo.steamtowerenhance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.steamtowerenhance.domain.Player;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【player】的数据库操作Service
* @createDate 2023-12-08 17:10:18
*/
public interface PlayerService extends IService<Player> {
    void insertBatch(Collection<Player> players);

    List<String> findAllPlayerSteamIds();

    List<String> findPlayerSteamIds(Integer offset, Integer pageSize);

    Integer countAllPlayers();
}
