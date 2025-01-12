package com.ruoyi.cloud.common.security.annotation;

import java.lang.annotation.*;

/**
 * @Author Roc
 * @Date 2025/01/12 21:07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerAuth {
    /**
     * 是否校验用户信息
     */
    boolean isUser() default false;
}
