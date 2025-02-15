package org.example.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.authorization.wechat.WechatUserRequestEntityConverter;
import org.example.authorization.wechat.WechatUserResponseConverter;
import org.example.entity.Oauth2ThirdAccount;
import org.example.exception.InvalidCaptchaException;
import org.example.service.IOauth2ThirdAccountService;
import org.example.strategy.context.Oauth2UserConverterContext;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author Roc
 * @Date 2025/2/12 15:50
 */
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final IOauth2ThirdAccountService thirdAccountService;

    private final Oauth2UserConverterContext userConverterContext;

    public CustomOauth2UserService(IOauth2ThirdAccountService thirdAccountService, Oauth2UserConverterContext userConverterContext) {
        this.thirdAccountService = thirdAccountService;
        this.userConverterContext = userConverterContext;
        // 初始化时添加微信用户信息请求处理，oidcUserService本质上是调用该类获取用户信息的，不用添加
        super.setRequestEntityConverter(new WechatUserRequestEntityConverter());
        // 设置用户信息转换器
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        List<HttpMessageConverter<?>> messageConverters = List.of(
                new StringHttpMessageConverter(),
                // 获取微信用户信息时使其支持“text/plain”
                new WechatUserResponseConverter(),
                new ResourceHttpMessageConverter(),
                new ByteArrayHttpMessageConverter(),
                new AllEncompassingFormHttpMessageConverter()
        );
        restTemplate.setMessageConverters(messageConverters);
        super.setRestOperations(restTemplate);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            // 转为项目中的三方用户信息
            Oauth2ThirdAccount oauth2ThirdAccount = userConverterContext.convert(userRequest, oAuth2User);
            // 检查用户信息
            thirdAccountService.checkAndSaveUser(oauth2ThirdAccount);
            // 将loginType设置至attributes中
            LinkedHashMap<String, Object> attributes = new LinkedHashMap<>(oAuth2User.getAttributes());
            // 将RegistrationId当做登录类型
            attributes.put("loginType", userRequest.getClientRegistration().getRegistrationId());
            String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                    .getUserNameAttributeName();
            return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, userNameAttributeName);
        } catch (Exception e) {
            // 获取当前request
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                throw new InvalidCaptchaException("Failed to get the current request.");
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            // 将异常放入session中
            request.getSession(Boolean.FALSE).setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, e);
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }
}
