package org.example.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author Roc
 * @Date 2025/2/5 10:46
 */
public class InvalidCaptchaException extends AuthenticationException {

    public InvalidCaptchaException(String msg) {
        super(msg);
    }
}
