package com.roc.springall.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TestController
 * @Author dongp
 * @Date 2022/7/18 0018 19:18
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "hello spring boot";
    }
}
