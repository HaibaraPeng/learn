package org.example.strategy;

import org.example.entity.Oauth2ThirdAccount;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @Author Roc
 * @Date 2025/2/12 16:14
 */
public interface Oauth2UserConverterStrategy {

    /**
     * 将oauth2登录的认证信息转为 {@link Oauth2ThirdAccount}
     *
     * @param oAuth2User oauth2登录获取的用户信息
     * @return 项目中的用户信息
     */
    Oauth2ThirdAccount convert(OAuth2User oAuth2User);
}
