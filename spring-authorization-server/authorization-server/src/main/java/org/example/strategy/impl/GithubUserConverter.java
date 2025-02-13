package org.example.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.Oauth2ThirdAccount;
import org.example.strategy.Oauth2UserConverterStrategy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import static org.example.constant.SecurityConstants.THIRD_LOGIN_GITHUB;

/**
 * @Author Roc
 * @Date 2025/2/12 16:23
 */
@RequiredArgsConstructor
@Component(THIRD_LOGIN_GITHUB)
public class GithubUserConverter implements Oauth2UserConverterStrategy {

    private final GiteeUserConverter userConverter;

    protected static final String LOGIN_TYPE = THIRD_LOGIN_GITHUB;

    @Override
    public Oauth2ThirdAccount convert(OAuth2User oAuth2User) {
        // github与gitee目前所取字段一致，直接调用gitee的解析
        Oauth2ThirdAccount thirdAccount = userConverter.convert(oAuth2User);
        // 提取location
        Object location = oAuth2User.getAttributes().get("location");
        thirdAccount.setLocation(String.valueOf(location));
        // 设置登录类型
        thirdAccount.setType(LOGIN_TYPE);
        return thirdAccount;
    }
}
