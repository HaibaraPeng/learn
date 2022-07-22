package com.roc.springall.demo.controller;

import com.roc.springall.demo.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Description TestController
 * @Author dongp
 * @Date 2022/7/22 0022 17:40
 */
@RestController
public class TestController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HelloService helloService;

    @GetMapping("test1")
    public String test1() throws InterruptedException {
        // 休眠1秒
        TimeUnit.SECONDS.sleep(1);
        return "test1";
    }


    @GetMapping("test2")
    public String test2() {
        return "test2 " + helloService.hello();
    }
}
