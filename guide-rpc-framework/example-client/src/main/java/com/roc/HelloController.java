package com.roc;

import com.roc.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description HelloController
 * @Author dongp
 * @Date 2022/8/9 0009 15:20
 */
@Slf4j
@Component
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    public void test() {
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println("hello : " + hello);
    }
}
