package com.ruoyi.cloud.modules.system.controller;

import com.ruoyi.cloud.api.system.domain.SysUser;
import com.ruoyi.cloud.common.core.web.controller.BaseController;
import com.ruoyi.cloud.common.core.web.domain.AjaxResult;
import com.ruoyi.cloud.common.security.utils.SecurityUtils;
import com.ruoyi.cloud.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * @Author Roc
 * @Date 2025/01/15 22:46
 */
@RestController
@RequestMapping("/user/profile")
public class SysProfileController extends BaseController {
    @Autowired
    private ISysUserService userService;

//    @Autowired
//    private TokenService tokenService;
//
//    @Autowired
//    private RemoteFileService remoteFileService;

    /**
     * 个人信息
     */
    @GetMapping
    public AjaxResult profile() {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(username));
        ajax.put("postGroup", userService.selectUserPostGroup(username));
        return ajax;
    }

//    /**
//     * 修改用户
//     */
//    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult updateProfile(@RequestBody SysUser user) {
//        LoginUser loginUser = SecurityUtils.getLoginUser();
//        SysUser currentUser = loginUser.getSysUser();
//        currentUser.setNickName(user.getNickName());
//        currentUser.setEmail(user.getEmail());
//        currentUser.setPhonenumber(user.getPhonenumber());
//        currentUser.setSex(user.getSex());
//        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser)) {
//            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
//        }
//        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser)) {
//            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
//        }
//        if (userService.updateUserProfile(currentUser)) {
//            // 更新缓存用户信息
//            tokenService.setLoginUser(loginUser);
//            return success();
//        }
//        return error("修改个人信息异常，请联系管理员");
//    }
//
//    /**
//     * 重置密码
//     */
//    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
//    @PutMapping("/updatePwd")
//    public AjaxResult updatePwd(String oldPassword, String newPassword) {
//        String username = SecurityUtils.getUsername();
//        SysUser user = userService.selectUserByUserName(username);
//        String password = user.getPassword();
//        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
//            return error("修改密码失败，旧密码错误");
//        }
//        if (SecurityUtils.matchesPassword(newPassword, password)) {
//            return error("新密码不能与旧密码相同");
//        }
//        newPassword = SecurityUtils.encryptPassword(newPassword);
//        if (userService.resetUserPwd(username, newPassword) > 0) {
//            // 更新缓存用户密码
//            LoginUser loginUser = SecurityUtils.getLoginUser();
//            loginUser.getSysUser().setPassword(newPassword);
//            tokenService.setLoginUser(loginUser);
//            return success();
//        }
//        return error("修改密码异常，请联系管理员");
//    }
//
//    /**
//     * 头像上传
//     */
//    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
//    @PostMapping("/avatar")
//    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) {
//        if (!file.isEmpty()) {
//            LoginUser loginUser = SecurityUtils.getLoginUser();
//            String extension = FileTypeUtils.getExtension(file);
//            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
//                return error("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
//            }
//            R<SysFile> fileResult = remoteFileService.upload(file);
//            if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
//                return error("文件服务异常，请联系管理员");
//            }
//            String url = fileResult.getData().getUrl();
//            if (userService.updateUserAvatar(loginUser.getUsername(), url)) {
//                AjaxResult ajax = AjaxResult.success();
//                ajax.put("imgUrl", url);
//                // 更新缓存用户头像
//                loginUser.getSysUser().setAvatar(url);
//                tokenService.setLoginUser(loginUser);
//                return ajax;
//            }
//        }
//        return error("上传图片异常，请联系管理员");
//    }
}
