package com.ruoyi.cloud.common.core.exception;

/**
 * @Author Roc
 * @Date 2025/01/12 21:12
 */
public class InnerAuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InnerAuthException(String message) {
        super(message);
    }
}
