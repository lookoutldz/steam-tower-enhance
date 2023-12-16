package org.demo.steamtowerenhance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.steamtowerenhance.domain.Player;
import org.demo.steamtowerenhance.service.PlayerService;
import org.demo.steamtowerenhance.mapper.PlayerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【player】的数据库操作Service实现
* @createDate 2023-12-08 17:10:18
*/
@Service
public class PlayerServiceImpl extends ServiceImpl<PlayerMapper, Player>
    implements PlayerService{

    @Transactional
    @Override
    public void insertBatch(Collection<Player> players) {
        baseMapper.insertBatch(players);
    }

    @Override
    public List<String> findAllPlayerSteamIds() {
        return baseMapper.findAllPlayerSteamIds();
    }

    @Override
    public List<String> findPlayerSteamIds(Integer offset, Integer pageSize) {
        return baseMapper.findPlayerSteamIds(offset, pageSize);
    }

    @Override
    public Integer countAllPlayers() {
        return baseMapper.countAllPlayers();
    }
}




