package com.roc.shop.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.sys.constant.Constant;
import com.roc.shop.sys.dao.SysMenuMapper;
import com.roc.shop.sys.model.SysMenu;
import com.roc.shop.sys.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description SysMenuServiceImpl
 * @Author roc
 * @Date 2022/11/22 下午10:27
 */
@RequiredArgsConstructor
@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> listMenuByUserId(Long userId) {
        // 用户的所有菜单信息
        List<SysMenu> sysMenus;
        // 系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN_ID) {
            sysMenus = sysMenuMapper.listMenu();
        } else {
            sysMenus = sysMenuMapper.listMenuByUserId(userId);
        }

        Map<Long, List<SysMenu>> sysMenuLevelMap = sysMenus.stream()
                .sorted(Comparator.comparing(SysMenu::getOrderNum))
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 一级菜单
        List<SysMenu> rootMenu = sysMenuLevelMap.get(0L);
        if (CollectionUtil.isEmpty(rootMenu)) {
            return Collections.emptyList();
        }
        // 二级菜单
        for (SysMenu sysMenu : rootMenu) {
            sysMenu.setList(sysMenuLevelMap.get(sysMenu.getMenuId()));
        }
        return rootMenu;
    }
}
