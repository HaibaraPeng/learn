package com.roc.shop.security.common.config;

import cn.hutool.core.util.ArrayUtil;
import com.roc.shop.security.common.adapter.AuthConfigAdapter;
import com.roc.shop.security.common.adapter.DefaultAuthConfigAdapter;
import com.roc.shop.security.common.filter.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import javax.servlet.DispatcherType;

/**
 * @Description AuthConfig
 * @Author roc
 * @Date 2022/11/24 下午10:13
 */
@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig {

    private final AuthFilter authFilter;

    @Bean
    @ConditionalOnMissingBean
    public AuthConfigAdapter authConfigAdapter() {
        return new DefaultAuthConfigAdapter();
    }

    @Bean
    @Lazy
    public FilterRegistrationBean<AuthFilter> filterRegistration(AuthConfigAdapter authConfigAdapter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        // 添加过滤器
        registration.setFilter(authFilter);
        // 设置过滤路径，/*所有路径
        registration.addUrlPatterns(ArrayUtil.toArray(authConfigAdapter.pathPatterns(), String.class));
        registration.setName("authFilter");
        // 设置优先级
        registration.setOrder(0);
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        return registration;
    }
}
