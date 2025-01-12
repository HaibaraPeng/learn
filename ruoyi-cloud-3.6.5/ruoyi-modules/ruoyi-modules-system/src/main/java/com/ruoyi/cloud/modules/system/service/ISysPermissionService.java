package com.ruoyi.cloud.modules.system.service;

import com.ruoyi.cloud.api.system.domain.SysUser;

import java.util.Set;

/**
 * @Author Roc
 * @Date 2025/01/12 17:03
 */
public interface ISysPermissionService {
    /**
     * 获取角色数据权限
     *
     * @param userId 用户Id
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user);

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户Id
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user);
}
