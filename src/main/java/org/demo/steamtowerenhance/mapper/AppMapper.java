package org.demo.steamtowerenhance.mapper;

import org.apache.ibatis.annotations.Param;
import org.demo.steamtowerenhance.domain.App;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
* @author amos
* @description 针对表【app】的数据库操作Mapper
* @createDate 2023-12-08 09:39:18
* @Entity org.demo.steamtowerenhance.domain.App
*/
@Repository
public interface AppMapper extends BaseMapper<App> {

    void insertBatch(Collection<App> appList);

    void updateByPrimaryKeySelective(App app);

    List<Integer> findAppIds(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
}




