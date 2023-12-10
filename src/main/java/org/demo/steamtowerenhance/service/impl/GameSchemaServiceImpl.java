package org.demo.steamtowerenhance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.steamtowerenhance.domain.GameSchema;
import org.demo.steamtowerenhance.service.GameSchemaService;
import org.demo.steamtowerenhance.mapper.GameSchemaMapper;
import org.springframework.stereotype.Service;

/**
* @author amos
* @description 针对表【game_schema】的数据库操作Service实现
* @createDate 2023-12-08 17:10:18
*/
@Service
public class GameSchemaServiceImpl extends ServiceImpl<GameSchemaMapper, GameSchema>
    implements GameSchemaService{

}




