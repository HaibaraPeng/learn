package com.ruoyi.cloud.common.core.exception;

/**
 * @Author Roc
 * @Date 2025/01/07 22:19
 */
public class CaptchaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CaptchaException(String msg) {
        super(msg);
    }
}
