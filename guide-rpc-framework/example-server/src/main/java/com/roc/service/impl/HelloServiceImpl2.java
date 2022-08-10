package com.roc.service.impl;

import com.roc.Hello;
import com.roc.HelloService;
import com.roc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description HelloServiceImpl2
 * @Author penn
 * @Date 2022/7/31 10:26
 */
@Slf4j
@RpcService(group = "test2", version = "version2")
public class HelloServiceImpl2 implements HelloService {
    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl2返回: {}.", result);
        return result;
    }
}
