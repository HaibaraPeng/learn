package com.roc.loadbalance.loadbalancer;

import com.roc.loadbalance.AbstractLoadBalance;
import com.roc.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @Description implementation of random load balancing strategy
 * @Author dongp
 * @Date 2022/8/11 0011 17:31
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceUrlList.get(random.nextInt(serviceUrlList.size()));
    }
}
