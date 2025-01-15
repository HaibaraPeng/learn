package com.ruoyi.cloud.common.core.exception.auth;

/**
 * @Author Roc
 * @Date 2025/01/15 22:21
 */
public class NotLoginException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotLoginException(String message) {
        super(message);
    }
}
