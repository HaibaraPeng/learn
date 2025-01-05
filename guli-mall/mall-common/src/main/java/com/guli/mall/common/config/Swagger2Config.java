package com.guli.mall.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/23 17:05
 */
@Configuration
@EnableSwagger2WebMvc
public class Swagger2Config {

    @Bean
    public Docket authApiConfig() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name("userId")
                .description("token")
                //.defaultValue(JwtHelper.createToken(1L, "admin"))
                .defaultValue("1")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();
        pars.add(tokenPar.build());

        Docket cartApi = new Docket(DocumentationType.SWAGGER_2)
                .groupName("authApi")
                .apiInfo(apiInfo())
                .select()
                //只显示api路径下的页面
                // /api/user/login
                // /admin/order/findAll
                // /add/PERSON/all
                .apis(RequestHandlerSelectors.basePackage("com.guli.mall.auth"))
//                .paths(PathSelectors.regex("/cart/.*"))
                .build()
                .globalOperationParameters(pars);
        return cartApi;
    }

    @Bean
    public Docket cartApiConfig() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name("userId")
                .description("token")
                //.defaultValue(JwtHelper.createToken(1L, "admin"))
                .defaultValue("1")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();
        pars.add(tokenPar.build());

        Docket cartApi = new Docket(DocumentationType.SWAGGER_2)
                .groupName("cartApi")
                .apiInfo(apiInfo())
                .select()
                //只显示api路径下的页面
                // /api/user/login
                // /admin/order/findAll
                // /add/PERSON/all
                .apis(RequestHandlerSelectors.basePackage("com.guli.mall.cart"))
                .paths(PathSelectors.regex("/cart/.*"))
                .build()
                .globalOperationParameters(pars);
        return cartApi;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("网站-API文档")
                .description("本文档描述了谷粒商城微服务接口定义")
                .version("1.0")
                .contact(new Contact("guili", "http://guili.com", "guili"))
                .build();
    }

}
