package com.ruoyi.cloud.modules.job;

import com.ruoyi.cloud.common.security.annotation.EnableCustomConfig;
import com.ruoyi.cloud.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author Roc
 * @Date 2025/02/15 22:34
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class JobApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}
