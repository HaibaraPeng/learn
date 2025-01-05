package com.guli.mall.member.exception;

/**
 * @Author Roc
 * @Date 2025/01/05 22:37
 */
public class PhoneException extends RuntimeException {
    public PhoneException() {
        super("存在相同的手机号");
    }
}
