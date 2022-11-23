package com.roc.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roc.shop.common.exception.YamiShopBindException;
import com.roc.shop.security.admin.dto.CaptchaAuthenticationDTO;
import com.roc.shop.security.common.bo.UserInfoInTokenBO;
import com.roc.shop.security.common.enums.SysTypeEnum;
import com.roc.shop.security.common.manager.PasswordCheckManager;
import com.roc.shop.security.common.manager.PasswordManager;
import com.roc.shop.security.common.manager.TokenStore;
import com.roc.shop.security.common.vo.TokenInfoVO;
import com.roc.shop.sys.constant.Constant;
import com.roc.shop.sys.model.SysMenu;
import com.roc.shop.sys.model.SysUser;
import com.roc.shop.sys.service.SysMenuService;
import com.roc.shop.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description AdminLoginController
 * @Author roc
 * @Date 2022/11/21 下午10:12
 */
@RequiredArgsConstructor
@RestController
@Api(tags = "登录")
public class AdminLoginController {

    private final CaptchaService captchaService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final PasswordManager passwordManager;
    private final PasswordCheckManager passwordCheckManager;
    private final TokenStore tokenStore;

    @PostMapping("/adminLogin")
    @ApiOperation(value = "账号密码 + 验证码登录(用于后台登录)", notes = "通过账号/手机号/用户名密码登录")
    public ResponseEntity<?> login(
            @Valid @RequestBody CaptchaAuthenticationDTO captchaAuthenticationDTO) {
        // 登陆后台登陆需要再校验一遍验证码
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(captchaAuthenticationDTO.getCaptchaVerification());
        ResponseModel response = captchaService.verification(captchaVO);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body("验证码有误或已过期");
        }

        SysUser sysUser = sysUserService.getByUserName(captchaAuthenticationDTO.getUserName());
        if (sysUser == null) {
            throw new YamiShopBindException("账号或密码不正确");
        }

        // 半小时内密码输入错误十次，已限制登陆30分钟
        String decryptPassword = passwordManager.decryptPassword(captchaAuthenticationDTO.getPassWord());
        passwordCheckManager.checkPassword(SysTypeEnum.ADMIN, captchaAuthenticationDTO.getUserName(), decryptPassword, sysUser.getPassword());

        // 不是店铺超级管理员，并且是禁用状态，无法登陆
        if (Objects.equals(sysUser.getStatus(), 0)) {
            // 未找到此用户信息
            throw new YamiShopBindException("未找到此用户信息");
        }

        UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
        userInfoInToken.setUserId(String.valueOf(sysUser.getUserId()));
        userInfoInToken.setSysType(SysTypeEnum.ADMIN.value());
        userInfoInToken.setEnabled(sysUser.getStatus() == 1);
        userInfoInToken.setPerms(getUserPermissions(sysUser.getUserId()));
        userInfoInToken.setNickName(sysUser.getUsername());
        userInfoInToken.setShopId(sysUser.getShopId());
        // 存储token返回vo
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
        return ResponseEntity.ok(tokenInfoVO);
    }

    private Set<String> getUserPermissions(Long userId) {
        List<String> permsList;

        // 系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN_ID) {
            List<SysMenu> menuList = sysMenuService.list(Wrappers.emptyWrapper());
            permsList = menuList.stream().map(SysMenu::getPerms).collect(Collectors.toList());
        } else {
            permsList = sysUserService.queryAllPerms(userId);
        }
        return permsList.stream().flatMap((perms) -> {
            if (StrUtil.isBlank(perms)) {
                return null;
            }
            return Arrays.stream(perms.trim().split(StrUtil.COMMA));
        }).collect(Collectors.toSet());
    }

}
