package com.guli.mall.member.exception;

/**
 * @Author Roc
 * @Date 2025/01/05 22:37
 */
public class UsernameException extends RuntimeException {
    public UsernameException() {
        super("存在相同的用户名");
    }
}
