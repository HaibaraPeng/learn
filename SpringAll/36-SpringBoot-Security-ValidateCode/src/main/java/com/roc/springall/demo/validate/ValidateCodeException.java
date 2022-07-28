package com.roc.springall.demo.validate;

import org.springframework.security.core.AuthenticationException;

public class ValidateCodeException extends AuthenticationException {
    private static final long serialVersionUID = 5022575393500654458L;

    ValidateCodeException(String message) {
        super(message);
    }
}