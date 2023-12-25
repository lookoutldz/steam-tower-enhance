package org.demo.steamtowerenhance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.steamtowerenhance.domain.PlayerAchievements;
import org.demo.steamtowerenhance.mapper.PlayerAchievementMapper;
import org.demo.steamtowerenhance.service.PlayerAchievementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
* @author amos
* @description 针对表【player_achi】的数据库操作Service实现
* @createDate 2023-12-08 17:10:18
*/
@Service
public class PlayerAchievementServiceImpl extends ServiceImpl<PlayerAchievementMapper, PlayerAchievements>
    implements PlayerAchievementService {

    @Transactional
    @Override
    public void insertBatch(Collection<PlayerAchievements> playerAchievements) {
        baseMapper.insertBatch(playerAchievements);
    }
}




