package com.roc.shop.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roc.shop.sys.model.SysMenu;

import java.util.List;

/**
 * @Description SysMenuService
 * @Author roc
 * @Date 2022/11/22 下午10:25
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取用户菜单列表
     * @param userId 用户id
     * @return 菜单列表
     */
    List<SysMenu> listMenuByUserId(Long userId);
}
