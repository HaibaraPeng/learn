package com.roc.shop.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.sys.dao.SysMenuMapper;
import com.roc.shop.sys.model.SysMenu;
import com.roc.shop.sys.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Description SysMenuServiceImpl
 * @Author roc
 * @Date 2022/11/22 下午10:27
 */
@RequiredArgsConstructor
@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
}
