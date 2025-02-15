package org.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Roc
 * @Date 2025/2/14 10:19
 */
@RestController
public class TestController {

    @GetMapping("/test01")
    @PreAuthorize("hasAnyAuthority('message.write')")
    public String test01() {
        return "test01";
    }

    @GetMapping("/test02")
    @PreAuthorize("hasAnyAuthority('test02')")
    public String test02() {
        return "test02";
    }

    @GetMapping("/app")
    @PreAuthorize("hasAnyAuthority('app')")
    public String app() {
        return "app";
    }
}
