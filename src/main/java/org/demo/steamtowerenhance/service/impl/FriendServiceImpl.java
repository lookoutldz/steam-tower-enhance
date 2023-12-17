package org.demo.steamtowerenhance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.steamtowerenhance.domain.Friend;
import org.demo.steamtowerenhance.service.FriendService;
import org.demo.steamtowerenhance.mapper.FriendMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【friend】的数据库操作Service实现
* @createDate 2023-12-08 17:10:18
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

    @Transactional
    @Override
    public void insertBatch(Collection<Friend> friends) {
        baseMapper.insertBatch(friends);
    }

    @Override
    public Integer countByDistinctFriendsteamid() {
        return baseMapper.countByDistinctFriendsteamid();
    }

    @Override
    public List<String> findDistinctFriendSteamIds(Integer offset, Integer pageSize) {
        return baseMapper.findDistinctFriendSteamIds(offset, pageSize);
    }

    @Override
    public List<String> selectAllDistinctFriendsId() {
        return baseMapper.selectAllDistinctFriendsId();
    }
}




