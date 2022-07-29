package com.roc.registry.zk;

import com.roc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Description service registration  based on zookeeper
 * @Author dongp
 * @Date 2022/7/29 0029 14:33
 */
@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {

    }
}
