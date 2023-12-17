package org.demo.steamtowerenhance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.steamtowerenhance.domain.Friend;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【friend】的数据库操作Service
* @createDate 2023-12-08 17:10:18
*/
public interface FriendService extends IService<Friend> {
    void insertBatch(Collection<Friend> friends);

    Integer countByDistinctFriendsteamid();

    List<String> findDistinctFriendSteamIds(Integer offset, Integer pageSize);

    List<String> selectAllDistinctFriendsId();
}
