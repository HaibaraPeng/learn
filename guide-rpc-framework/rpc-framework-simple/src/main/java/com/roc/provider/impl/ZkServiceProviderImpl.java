package com.roc.provider.impl;

import com.roc.config.RpcServiceConfig;
import com.roc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * @Description ZkServiceProviderImpl
 * @Author penn
 * @Date 2022/7/28 22:27
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {

    }

    @Override
    public Object getService(String rpcServiceName) {
        return null;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {

    }
}
