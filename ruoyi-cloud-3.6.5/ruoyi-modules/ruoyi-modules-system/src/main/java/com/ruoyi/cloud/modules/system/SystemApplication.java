package com.ruoyi.cloud.modules.system;

import com.ruoyi.cloud.common.security.annotation.EnableCustomConfig;
import com.ruoyi.cloud.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}