package com.roc.annotation;

import java.lang.annotation.*;

/**
 * @Description RPC reference annotation, autowire the service implementation class
 * @Author dongp
 * @Date 2022/8/9 0009 15:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * Service version, default value is empty string
     *
     * @return
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     *
     * @return
     */
    String group() default "";
}
