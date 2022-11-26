package com.roc.shop.security.common.util;

import com.roc.shop.security.common.bo.UserInfoInTokenBO;
import jdk.internal.misc.TerminatingThreadLocal;

/**
 * @Description AuthUserContext
 * @Author roc
 * @Date 2022/11/24 下午9:43
 */
public class AuthUserContext {

    private static final ThreadLocal<UserInfoInTokenBO> USER_INFO_IN_TOKEN_HOLDER = new TerminatingThreadLocal<>();

    public static UserInfoInTokenBO get() {
        return USER_INFO_IN_TOKEN_HOLDER.get();
    }

    public static void set(UserInfoInTokenBO userInfoInTokenBO) {
        USER_INFO_IN_TOKEN_HOLDER.set(userInfoInTokenBO);
    }

    public static void clean() {
        if (USER_INFO_IN_TOKEN_HOLDER.get() != null) {
            USER_INFO_IN_TOKEN_HOLDER.remove();
        }
    }
}
