package com.roc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description RpcConfigEnum
 * @Author dongp
 * @Date 2022/7/29 0029 16:40
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;
}
