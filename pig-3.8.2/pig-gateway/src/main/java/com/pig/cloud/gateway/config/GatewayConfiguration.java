package com.pig.cloud.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pig.cloud.gateway.filter.PigRequestGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Roc
 * @Date 2025/1/9 17:38
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

    /**
     * 创建PigRequest全局过滤器
     * @return PigRequest全局过滤器
     */
    @Bean
    public PigRequestGlobalFilter pigRequestGlobalFilter() {
        return new PigRequestGlobalFilter();
    }

//    /**
//     * 创建全局异常处理程序
//     * @param objectMapper 对象映射器
//     * @return 全局异常处理程序
//     */
//    @Bean
//    public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
//        return new GlobalExceptionHandler(objectMapper);
//    }

}
