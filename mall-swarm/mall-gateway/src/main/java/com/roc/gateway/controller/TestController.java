package com.roc.gateway.controller;

import com.roc.common.api.CommonResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author dongp
 * @Date 2022/7/28 0028 11:34
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public CommonResult test() {
        return CommonResult.success("hello world");
    }
}
