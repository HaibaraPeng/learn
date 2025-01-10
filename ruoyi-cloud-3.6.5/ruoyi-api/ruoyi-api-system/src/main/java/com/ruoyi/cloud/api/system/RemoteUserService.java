package com.ruoyi.cloud.api.system;

import com.ruoyi.cloud.api.system.domain.SysUser;
import com.ruoyi.cloud.api.system.factory.RemoteUserFallbackFactory;
import com.ruoyi.cloud.api.system.model.LoginUser;
import com.ruoyi.cloud.common.core.constant.SecurityConstants;
import com.ruoyi.cloud.common.core.constant.ServiceNameConstants;
import com.ruoyi.cloud.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Roc
 * @Date 2025/01/10 21:34
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source   请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source  请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 记录用户登录IP地址和登录时间
     *
     * @param sysUser 用户信息
     * @param source  请求来源
     * @return 结果
     */
    @PutMapping("/user/recordlogin")
    public R<Boolean> recordUserLogin(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
