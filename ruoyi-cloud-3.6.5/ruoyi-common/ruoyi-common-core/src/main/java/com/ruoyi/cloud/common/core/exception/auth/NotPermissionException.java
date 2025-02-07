package com.ruoyi.cloud.common.core.exception.auth;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author Roc
 * @Date 2025/02/07 21:33
 */
public class NotPermissionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotPermissionException(String permission) {
        super(permission);
    }

    public NotPermissionException(String[] permissions) {
        super(StringUtils.join(permissions, ","));
    }
}
