package com.guigu.ssyx.service.sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Roc
 * @Date 2024/12/24 17:41
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSysApplication.class, args);
    }
}
