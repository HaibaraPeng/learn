package com.roc.shop.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roc.shop.sys.model.SysMenu;

import java.util.List;

/**
 * @Description SysMenuMapper
 * @Author roc
 * @Date 2022/11/22 下午10:27
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询用户的所有菜单ID
     * @param userId 用户id
     * @return 该用户所有可用的菜单
     */
    List<SysMenu> listMenuByUserId(Long userId);

    /**
     * 获取系统的所有菜单
     * @return 系统的所有菜单
     */
    List<SysMenu> listMenu();
}
