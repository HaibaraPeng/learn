package com.roc.gateway.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MrBird
 */
@RestController
public class UserController {

    @GetMapping("test")
    public String test(){
        return "hello world";
    }

    @GetMapping("index")
    public Object index(Authentication authentication){
        return authentication;
    }
}
