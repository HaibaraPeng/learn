package org.example.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.Oauth2ThirdAccount;
import org.example.strategy.Oauth2UserConverterStrategy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.example.constant.SecurityConstants.THIRD_LOGIN_GITEE;
import static org.example.constant.SecurityConstants.THIRD_LOGIN_WECHAT;

/**
 * @Author Roc
 * @Date 2025/2/13 14:28
 */
@RequiredArgsConstructor
@Component(THIRD_LOGIN_WECHAT)
public class WechatUserConverter implements Oauth2UserConverterStrategy {

    @Override
    public Oauth2ThirdAccount convert(OAuth2User oAuth2User) {
        // 获取三方用户信息
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 转换至Oauth2ThirdAccount
        Oauth2ThirdAccount thirdAccount = new Oauth2ThirdAccount();
        thirdAccount.setUniqueId(String.valueOf(attributes.get("openid")));
        thirdAccount.setThirdUsername(oAuth2User.getName());
        thirdAccount.setType(THIRD_LOGIN_GITEE);
        thirdAccount.setLocation(attributes.get("province") + " " + attributes.get("city"));
        // 设置基础用户信息
        thirdAccount.setName(oAuth2User.getName());
        thirdAccount.setAvatarUrl(String.valueOf(attributes.get("headimgurl")));
        return thirdAccount;
    }
}
