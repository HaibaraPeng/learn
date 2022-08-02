package com.roc.remoting.transport.netty.server;

import com.roc.config.RpcServiceConfig;
import com.roc.factory.SingletonFactory;
import com.roc.provider.ServiceProvider;
import com.roc.provider.impl.ZkServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description Server. Receive the client message, call the corresponding method according to the client message,
 * and then return the result to the client.
 * @Author dongp
 * @Date 2022/7/29 0029 17:55
 */
@Slf4j
@Component
public class NettyRpcServer {

    public static final int PORT = 9998;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    public void start() {

    }
}
