package com.guigu.cloud.feign.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope // 动态刷新
public class FeignOrder80Application {
    public static void main(String[] args) {
        SpringApplication.run(FeignOrder80Application.class, args);
    }
}