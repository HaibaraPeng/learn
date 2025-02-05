package org.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Roc
 * @Date 2025/1/14 11:51
 */
@RestController
public class TestController {

    @GetMapping("/test1")
    @PreAuthorize("hasAuthority('SCOPE_message.read')")
    public String test1() {
        System.out.println("test1============================================");
        return "test1";
    }
}
