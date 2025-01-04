package com.guigu.cloud.seata.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.guigu.cloud.seata.account.mapper") //import tk.mybatis.spring.annotation.MapperScan;
@EnableDiscoveryClient //服务注册和发现
@EnableFeignClients(basePackages = {"com.guigu.cloud.common.apis"})
public class SeataAccount2003Application {
    public static void main(String[] args) {
        SpringApplication.run(SeataAccount2003Application.class, args);
    }
}