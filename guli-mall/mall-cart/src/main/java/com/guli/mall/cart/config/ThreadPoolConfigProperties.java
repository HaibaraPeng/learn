package com.guli.mall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author Roc
 * @Date 2025/01/05 13:53
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
public class ThreadPoolConfigProperties {

    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;


}
