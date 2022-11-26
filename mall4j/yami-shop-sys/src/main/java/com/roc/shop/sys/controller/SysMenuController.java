package com.roc.shop.sys.controller;

import cn.hutool.core.map.MapUtil;
import com.roc.shop.security.admin.util.SecurityUtils;
import com.roc.shop.sys.model.SysMenu;
import com.roc.shop.sys.service.SysMenuService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description 系统菜单
 * @Author roc
 * @Date 2022/11/23 下午11:06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/nav")
    @ApiOperation(value="获取用户所拥有的菜单和权限", notes="通过登陆用户的userId获取用户所拥有的菜单和权限")
    public ResponseEntity<Map<Object, Object>> nav(){
        List<SysMenu> menuList = sysMenuService.listMenuByUserId(SecurityUtils.getSysUser().getUserId());

        return ResponseEntity.ok(MapUtil.builder().put("menuList", menuList).put("authorities", SecurityUtils.getSysUser().getAuthorities()).build());
    }
}
