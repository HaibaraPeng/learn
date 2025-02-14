package com.pig.cloud.upms.admin;

import com.pig.cloud.common.feign.annotation.EnablePigFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Roc
 * @Date 2025/2/14 16:06
 */
// TODO
//@EnablePigDoc(value = "admin")
@EnablePigFeignClients
//@EnablePigResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class PigAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(PigAdminApplication.class, args);
    }

}
