package com.roc.shop.security.admin.util;

import com.roc.shop.security.admin.model.YamiSysUser;
import com.roc.shop.security.common.bo.UserInfoInTokenBO;
import com.roc.shop.security.common.util.AuthUserContext;
import lombok.experimental.UtilityClass;

/**
 * @Description SecurityUtils
 * @Author roc
 * @Date 2022/11/24 下午9:41
 */
@UtilityClass
public class SecurityUtils {

    /**
     * 获取用户
     */
    public YamiSysUser getSysUser() {
        UserInfoInTokenBO userInfoInTokenBO = AuthUserContext.get();

        YamiSysUser details = new YamiSysUser();
        details.setUserId(Long.valueOf(userInfoInTokenBO.getUserId()));
        details.setEnabled(userInfoInTokenBO.getEnabled());
        details.setUsername(userInfoInTokenBO.getNickName());
        details.setAuthorities(userInfoInTokenBO.getPerms());
        details.setShopId(userInfoInTokenBO.getShopId());
        return details;
    }
}
