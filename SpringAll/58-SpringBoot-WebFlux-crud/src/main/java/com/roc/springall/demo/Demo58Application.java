package com.roc.springall.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * @Description Demo58Application
 * @Author dongp
 * @Date 2022/7/20 0020 11:36
 */
@SpringBootApplication
@EnableReactiveMongoRepositories
public class Demo58Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo58Application.class, args);
    }
}
