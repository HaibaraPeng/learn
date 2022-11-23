package com.roc.shop.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roc.shop.sys.model.SysUser;

import java.util.List;

/**
 * @Description SysUserService
 * @Author roc
 * @Date 2022/11/21 下午10:26
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    SysUser getByUserName(String username);

    /**
     * 查询用户的所有权限
     * @param userId  用户ID
     */
    List<String> queryAllPerms(Long userId);
}
