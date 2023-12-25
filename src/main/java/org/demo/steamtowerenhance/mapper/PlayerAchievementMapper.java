package org.demo.steamtowerenhance.mapper;

import org.demo.steamtowerenhance.domain.PlayerAchievements;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author amos
* @description 针对表【player_achi】的数据库操作Mapper
* @createDate 2023-12-08 17:10:18
* @Entity org.demo.steamtowerenhance.domain.PlayerAchi
*/
@Repository
public interface PlayerAchievementMapper extends BaseMapper<PlayerAchievements> {

}




