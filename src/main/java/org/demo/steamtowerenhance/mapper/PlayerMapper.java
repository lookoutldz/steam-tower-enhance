package org.demo.steamtowerenhance.mapper;

import org.apache.ibatis.annotations.Param;
import org.demo.steamtowerenhance.domain.Player;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【player】的数据库操作Mapper
* @createDate 2023-12-08 17:10:18
* @Entity org.demo.steamtowerenhance.domain.Player
*/
@Repository
public interface PlayerMapper extends BaseMapper<Player> {

    void insertBatch(Collection<Player> players);

    List<String> findAllPlayerSteamIds();

    List<String> findPlayerSteamIds(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer countAllPlayers();
}




