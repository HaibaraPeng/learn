package com.roc.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @Description 资源服务器配置
 * @Author dongp
 * @Date 2022/7/28 0028 17:06
 */
@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                .and().csrf().disable();
        return http.build();
    }
}
