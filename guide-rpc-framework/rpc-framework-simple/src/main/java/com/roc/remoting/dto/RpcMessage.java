package com.roc.remoting.dto;

import lombok.*;

/**
 * @Description RpcMessage
 * @Author dongp
 * @Date 2022/8/8 0008 15:17
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * rpc message type
     */
    private byte messageType;

    /**
     * serialization type
     */
    private byte codec;

    /**
     * compress type
     */
    private byte compress;

    /**
     * request id
     */
    private int requestId;

    /**
     * request data
     */
    private Object data;
}
