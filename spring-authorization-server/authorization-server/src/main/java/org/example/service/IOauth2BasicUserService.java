package org.example.service;

import org.example.entity.Oauth2BasicUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.Oauth2ThirdAccount;
import org.example.model.response.Oauth2UserinfoResult;

/**
 * <p>
 * 基础用户信息表 服务类
 * </p>
 *
 * @author Roc
 * @since 2025-02-08
 */
public interface IOauth2BasicUserService extends IService<Oauth2BasicUser> {

    /**
     * 生成用户信息
     *
     * @param thirdAccount 三方用户信息
     * @return 用户id
     */
    Integer saveByThirdAccount(Oauth2ThirdAccount thirdAccount);

    /**
     * 获取当前登录用户的信息
     *
     * @return 用户信息
     */
    Oauth2UserinfoResult getLoginUserInfo();

}
