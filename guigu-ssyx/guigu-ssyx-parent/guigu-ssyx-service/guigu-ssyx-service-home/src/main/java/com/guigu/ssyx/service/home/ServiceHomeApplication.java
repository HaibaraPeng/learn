package com.guigu.ssyx.service.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Roc
 * @Date 2024/12/24 17:41
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.guigu.ssyx.client.activity", "com.guigu.ssyx.client.product",
        "com.guigu.ssyx.client.search", "com.guigu.ssyx.client.user"})
public class ServiceHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHomeApplication.class, args);
    }
}
