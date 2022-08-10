package com.roc.remoting.transport;

import com.roc.extension.SPI;
import com.roc.remoting.dto.RpcRequest;

/**
 * @Description send RpcRequest
 * @Author dongp
 * @Date 2022/8/9 0009 16:45
 */
@SPI
public interface RpcRequestTransport {

    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest
     * @return
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
