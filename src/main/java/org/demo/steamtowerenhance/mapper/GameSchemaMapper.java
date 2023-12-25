package org.demo.steamtowerenhance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.demo.steamtowerenhance.domain.GameSchema;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
* @author amos
* @description 针对表【game_schema】的数据库操作Mapper
* @createDate 2023-12-08 17:10:18
* @Entity org.demo.steamtowerenhance.domain.GameSchema
*/
@Repository
public interface GameSchemaMapper extends BaseMapper<GameSchema> {

    void insertBatch(Collection<GameSchema> gameSchemas);
}




