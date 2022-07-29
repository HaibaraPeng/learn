package com.roc.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Description scan custom annotations
 * @Author dongp
 * @Date 2022/7/29 0029 18:12
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import()
@Documented
public @interface RpcScan {

    String[] basePackage();
}
