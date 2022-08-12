package com.roc.loadbalance;

import com.roc.remoting.dto.RpcRequest;
import com.roc.utils.CollectionUtil;

import java.util.List;

/**
 * @Description abstract class for a load balancing policy
 * @Author dongp
 * @Date 2022/8/11 0011 17:21
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            return null;
        }
        if (serviceUrlList.size() == 1) {
            return serviceUrlList.get(0);
        }
        return doSelect(serviceUrlList, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest);
}
