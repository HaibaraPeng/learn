package com.roc.registry.zk;

import com.roc.extension.ExtensionLoader;
import com.roc.loadbalance.LoadBalance;
import com.roc.registry.ServiceDiscovery;
import com.roc.registry.zk.util.CuratorUtils;
import com.roc.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @Description service discovery based on zookeeper
 * @Author dongp
 * @Date 2022/8/11 0011 17:08
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZKClient();
        return null;
    }
}
