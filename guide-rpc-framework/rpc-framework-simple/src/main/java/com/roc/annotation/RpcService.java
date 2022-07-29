package com.roc.annotation;

import java.lang.annotation.*;

/**
 * @Description RPC service annotation, marked on the service implementation class
 * @Author dongp
 * @Date 2022/7/29 0029 18:52
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

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
