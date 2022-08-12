package com.roc.loadbalance;

import com.roc.extension.SPI;
import com.roc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @Description interface to the load balancing policy
 * @Author dongp
 * @Date 2022/8/11 0011 17:18
 */
@SPI
public interface LoadBalance {

    /**
     * choose one from the list of existing service addresses list
     *
     * @param serviceUrlList
     * @param rpcRequest
     * @return
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
