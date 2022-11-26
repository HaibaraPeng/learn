package com.roc.shop.security.common.permission;

import cn.hutool.core.util.StrUtil;
import com.roc.shop.security.common.util.AuthUserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

/**
 * @Description 接口权限判断工具
 * @Author roc
 * @Date 2022/11/26 下午3:19
 */
@Slf4j
@Component("pms")
public class PermissionService {

    /**
     * 判断接口是否有xxx:xxx权限
     *
     * @param permission 权限
     * @return {boolean}
     */
    public boolean hasPermission(String permission) {
        if (StrUtil.isBlank(permission)) {
            return false;
        }
        return AuthUserContext.get().getPerms()
                .stream()
                .filter(StringUtils::hasText)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(permission, x));
    }
}
