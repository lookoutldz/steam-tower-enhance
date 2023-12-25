package org.demo.steamtowerenhance.service;

import org.apache.ibatis.annotations.Param;
import org.demo.steamtowerenhance.domain.OwnedGame;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【owned_game】的数据库操作Service
* @createDate 2023-12-08 17:10:18
*/
public interface OwnedGameService extends IService<OwnedGame> {
    void insertBatch(Collection<OwnedGame> ownedGames);

    List<OwnedGame> findSteamIdAndAppId(Integer offset, Integer pageSize);
}
