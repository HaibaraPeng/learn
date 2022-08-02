package com.roc.config;

import lombok.*;

/**
 * @Description RpcServiceConfig
 * @Author penn
 * @Date 2022/7/28 22:19
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    /**
     * service version
     */
    private String version = "";

    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group = "";

    /**
     * target service
     */
    private Object service;

    public String getRpcServiceName() {
        return new StringBuilder().append(getServiceName()).append(this.group).append(this.version).toString();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
