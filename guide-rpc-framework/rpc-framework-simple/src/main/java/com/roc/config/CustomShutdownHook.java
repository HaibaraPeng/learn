package com.roc.config;

import com.roc.registry.zk.util.CuratorUtils;
import com.roc.remoting.transport.netty.server.NettyRpcServer;
import com.roc.utils.concurrent.threadpool.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * when the server is closed, do something such as unregister all services
 *
 * @Description CustomShutdownHook
 * @Author penn
 * @Date 2022/7/31 10:33
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    private CustomShutdownHook() {

    }

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress()
                        , NettyRpcServer.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZKClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {

            }
            ThreadPoolFactoryUtil.shutdownAllThreadPool();
        }));
    }

}
