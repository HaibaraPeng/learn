package com.pig.cloud.auth.support.core;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * @Author Roc
 * @Date 2025/1/11 11:52
 */
public class FormIdentityLoginConfigurer extends AbstractHttpConfigurer<FormIdentityLoginConfigurer, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {
        http
//                .formLogin(formLogin -> {
//                    formLogin.loginPage("/token/login");
//                    formLogin.loginProcessingUrl("/token/form");
//                    formLogin.failureHandler(new FormAuthenticationFailureHandler());
//
//                })
//                .logout(logout -> logout.logoutSuccessHandler(new SsoLogoutSuccessHandler())
//                        .deleteCookies("JSESSIONID")
//                        .invalidateHttpSession(true)) // SSO登出成功处理

                .csrf(AbstractHttpConfigurer::disable);
    }

}
