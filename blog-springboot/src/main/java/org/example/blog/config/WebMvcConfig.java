package org.example.blog.config;

import org.example.blog.handler.PageableHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description web mvc配置
 * @Author dongp
 * @Date 2022/10/26 0026 15:24
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PageableHandlerInterceptor());
    }
}
