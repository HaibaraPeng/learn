package org.example.strategy.impl;

import org.example.entity.Oauth2ThirdAccount;
import org.example.strategy.Oauth2UserConverterStrategy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.example.constant.SecurityConstants.THIRD_LOGIN_GITEE;

/**
 * @Author Roc
 * @Date 2025/2/12 16:15
 */
@Component(THIRD_LOGIN_GITEE)
public class GiteeUserConverter implements Oauth2UserConverterStrategy {

    @Override
    public Oauth2ThirdAccount convert(OAuth2User oAuth2User) {
        // 获取三方用户信息
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 转换至Oauth2ThirdAccount
        Oauth2ThirdAccount thirdAccount = new Oauth2ThirdAccount();
        thirdAccount.setUniqueId(oAuth2User.getName());
        thirdAccount.setThirdUsername(String.valueOf(attributes.get("login")));
        thirdAccount.setType(THIRD_LOGIN_GITEE);
        thirdAccount.setBlog(String.valueOf(attributes.get("blog")));
        // 设置基础用户信息
        thirdAccount.setName(String.valueOf(attributes.get("name")));
        thirdAccount.setAvatarUrl(String.valueOf(attributes.get("avatar_url")));
        return thirdAccount;
    }
}
