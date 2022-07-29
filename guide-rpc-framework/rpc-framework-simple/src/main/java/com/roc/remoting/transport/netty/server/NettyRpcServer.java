package com.roc.remoting.transport.netty.server;

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
}
