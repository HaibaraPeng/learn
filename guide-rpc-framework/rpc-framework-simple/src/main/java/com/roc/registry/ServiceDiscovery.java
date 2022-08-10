package com.roc.registry;

import com.roc.extension.SPI;
import com.roc.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @Description service discovery
 * @Author dongp
 * @Date 2022/8/9 0009 17:58
 */
@SPI
public interface ServiceDiscovery {

    /**
     * lookup service by rpcServiceName
     *
     * @param rpcRequest
     * @return
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
