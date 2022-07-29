package com.roc.exception;

import com.roc.enums.RpcErrorMessageEnum;

/**
 * @Description RpcException
 * @Author dongp
 * @Date 2022/7/29 0029 17:33
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
