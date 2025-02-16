package com.ruoyi.cloud.modules.gen;

import com.ruoyi.cloud.common.security.annotation.EnableCustomConfig;
import com.ruoyi.cloud.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author Roc
 * @Date 2025/02/16 16:10
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class GenApplication {
    public static void main(String[] args) {
        SpringApplication.run(GenApplication.class, args);
    }
}
