package com.roc.registry;

import com.roc.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Description ServiceRegistry
 * @Author penn
 * @Date 2022/7/28 22:35
 */
@SPI
public interface ServiceRegistry {

    /**
     * register service
     *
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
