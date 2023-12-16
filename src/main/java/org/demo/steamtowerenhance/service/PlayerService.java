package org.demo.steamtowerenhance.service;

import org.apache.ibatis.annotations.Param;
import org.demo.steamtowerenhance.domain.Player;
import com.baomidou.mybatisplus.extension.service.IService;

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

    List<String> findPlayerSteamIds(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countAllPlayers();
}
