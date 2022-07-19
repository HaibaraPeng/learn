package com.roc.springall.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

//@RestController
@Controller
public class UserController {

    @GetMapping(value = "test", consumes = "text/properties")
    @ResponseBody
    public Properties test(@RequestBody Properties properties) {
        return properties;
    }

    @GetMapping(value = "test1", consumes = "text/properties")
    @ResponseBody
    public Properties test1(Properties properties) {
        return properties;
    }
}
