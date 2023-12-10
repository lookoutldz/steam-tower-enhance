package org.demo.steamtowerenhance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.steamtowerenhance.domain.Player;
import org.demo.steamtowerenhance.service.PlayerService;
import org.demo.steamtowerenhance.mapper.PlayerMapper;
import org.springframework.stereotype.Service;

/**
* @author amos
* @description 针对表【player】的数据库操作Service实现
* @createDate 2023-12-08 17:10:18
*/
@Service
public class PlayerServiceImpl extends ServiceImpl<PlayerMapper, Player>
    implements PlayerService{

}




