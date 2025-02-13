package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.Oauth2ThirdAccount;
import org.example.service.IOauth2ThirdAccountService;
import org.example.strategy.context.Oauth2UserConverterContext;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;

/**
 * @Author Roc
 * @Date 2025/2/12 16:44
 */
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final IOauth2ThirdAccountService thirdAccountService;

    private final Oauth2UserConverterContext userConverterContext;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 获取三方用户信息
        OidcUser oidcUser = super.loadUser(userRequest);
        // 转为项目中的三方用户信息
        Oauth2ThirdAccount oauth2ThirdAccount = userConverterContext.convert(userRequest, oidcUser);
        // 检查用户信息
        thirdAccountService.checkAndSaveUser(oauth2ThirdAccount);
        OidcIdToken oidcIdToken = oidcUser.getIdToken();
        // 将loginType设置至attributes中
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>(oidcIdToken.getClaims());
        // 将RegistrationId当做登录类型
        attributes.put("loginType", userRequest.getClientRegistration().getRegistrationId());
        // 重新生成一个idToken
        OidcIdToken idToken = new OidcIdToken(oidcIdToken.getTokenValue(), oidcIdToken.getIssuedAt(), oidcIdToken.getExpiresAt(), attributes);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        // 重新生成oidcUser
        if (StringUtils.hasText(userNameAttributeName)) {
            return new DefaultOidcUser(oidcUser.getAuthorities(), idToken, oidcUser.getUserInfo(), userNameAttributeName);
        }
        return new DefaultOidcUser(oidcUser.getAuthorities(), idToken, oidcUser.getUserInfo());
    }
}
