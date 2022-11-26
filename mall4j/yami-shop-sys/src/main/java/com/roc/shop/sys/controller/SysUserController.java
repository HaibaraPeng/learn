package com.roc.shop.sys.controller;

import com.roc.shop.security.admin.util.SecurityUtils;
import com.roc.shop.sys.model.SysUser;
import com.roc.shop.sys.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description SysUserController
 * @Author roc
 * @Date 2022/11/26 上午11:11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<SysUser> info(){
        return ResponseEntity.ok(sysUserService.getSysUserById(SecurityUtils.getSysUser().getUserId()));
    }
}
