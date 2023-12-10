package org.demo.steamtowerenhance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.demo.steamtowerenhance.mapper")
public class SteamTowerEnhanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteamTowerEnhanceApplication.class, args);
    }

}
