package com.guigu.cloud.alibaba.sentinel.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

/**
 * @Author Roc
 * @Date 2025/01/01 18:33
 */
@Service
public class FlowLimitService {
    @SentinelResource(value = "common")
    public void common()
    {
        System.out.println("------FlowLimitService come in");
    }
}
