package com.pig.cloud.common.core.exception;

/**
 * @Author Roc
 * @Date 2025/1/11 11:09
 */
public class ValidateCodeException extends RuntimeException {

    private static final long serialVersionUID = -7285211528095468156L;

    public ValidateCodeException() {
    }

    public ValidateCodeException(String msg) {
        super(msg);
    }

}
