package com.roc.registry.zk;

import com.roc.registry.ServiceRegistry;
import com.roc.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @Description service registration  based on zookeeper
 * @Author dongp
 * @Date 2022/7/29 0029 14:33
 */
@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = new StringBuilder().append(CuratorUtils.ZK_REGISTER_ROOT_PATH)
                .append("/").append(rpcServiceName).append(inetSocketAddress.toString()).toString();
        CuratorFramework zkClient = CuratorUtils.getZKClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }

}
