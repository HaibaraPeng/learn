package com.roc.springall.demo.controller;

import com.roc.springall.demo.bean.BlogProperties;
import com.roc.springall.demo.bean.ConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TestController
 * @Author dongp
 * @Date 2022/7/18 0018 19:18
 */
@RestController
public class TestController {

    @Autowired
    private BlogProperties blogProperties;
    @Autowired
    private ConfigBean configBean;
//    @Autowired
//    private TestConfigBean testConfigBean;

    @GetMapping("/test")
    public String test() {
        return "hello spring boot";
    }

    @GetMapping("/blog")
    public String blog() {
        return blogProperties.getName() + ":" + blogProperties.getTitle();
    }

    @GetMapping("/blog2")
    public String blog2() {
        return configBean.getName() + ":" + configBean.getTitle();
    }
}
