package org.demo.steamtowerenhance.mapper;

import org.apache.ibatis.annotations.Param;
import org.demo.steamtowerenhance.domain.OwnedGame;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【owned_game】的数据库操作Mapper
* @createDate 2023-12-08 17:10:18
* @Entity org.demo.steamtowerenhance.domain.OwnedGame
*/
@Repository
public interface OwnedGameMapper extends BaseMapper<OwnedGame> {

    void insertBatch(Collection<OwnedGame> ownedGames);
}




