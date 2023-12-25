package org.demo.steamtowerenhance.service;

import org.demo.steamtowerenhance.domain.GameSchema;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

/**
* @author amos
* @description 针对表【game_schema】的数据库操作Service
* @createDate 2023-12-08 17:10:18
*/
public interface GameSchemaService extends IService<GameSchema> {
    void insertBatch(Collection<GameSchema> gameSchemas);
}
