package com.guigu.cloud.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.guigu.cloud.payment.mapper") //import tk.mybatis.spring.annotation.MapperScan;
@EnableDiscoveryClient
@RefreshScope // 动态刷新
public class Payment8001Application {
    public static void main(String[] args) {
        SpringApplication.run(Payment8001Application.class, args);
    }
}