package org.demo.steamtowerenhance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.steamtowerenhance.domain.App;
import org.demo.steamtowerenhance.service.AppService;
import org.demo.steamtowerenhance.mapper.AppMapper;
import org.springframework.stereotype.Service;

/**
* @author amos
* @description 针对表【app】的数据库操作Service实现
* @createDate 2023-12-08 09:39:18
*/
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>
    implements AppService{

}




