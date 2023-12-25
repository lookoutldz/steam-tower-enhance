package org.demo.steamtowerenhance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.steamtowerenhance.domain.OwnedGame;
import org.demo.steamtowerenhance.service.OwnedGameService;
import org.demo.steamtowerenhance.mapper.OwnedGameMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【owned_game】的数据库操作Service实现
* @createDate 2023-12-08 17:10:18
*/
@Service
public class OwnedGameServiceImpl extends ServiceImpl<OwnedGameMapper, OwnedGame>
    implements OwnedGameService{

    @Transactional
    @Override
    public void insertBatch(Collection<OwnedGame> ownedGames) {
        baseMapper.insertBatch(ownedGames);
    }
}




