package com.roc.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 自定义Oauth2获取令牌接口
 * Created by macro on 2020/7/17.
 */
@RestController
//@RequestMapping("/oauth")
public class AuthController {

//    @Autowired
//    private TokenEndpoint tokenEndpoint;

//    @ApiOperation("Oauth2获取token")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "grant_type", value = "授权模式", required = true),
//            @ApiImplicitParam(name = "client_id", value = "Oauth2客户端ID", required = true),
//            @ApiImplicitParam(name = "client_secret", value = "Oauth2客户端秘钥", required = true),
//            @ApiImplicitParam(name = "refresh_token", value = "刷新token"),
//            @ApiImplicitParam(name = "username", value = "登录用户名"),
//            @ApiImplicitParam(name = "password", value = "登录密码")
//    })
//    @RequestMapping(value = "/token", method = RequestMethod.POST)
//    public CommonResult<Oauth2TokenDto> postAccessToken(@ApiIgnore Principal principal, @ApiIgnore @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
//        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
//        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
//                .token(oAuth2AccessToken.getValue())
//                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
//                .expiresIn(oAuth2AccessToken.getExpiresIn())
//                .tokenHead(AuthConstant.JWT_TOKEN_PREFIX).build();
//
//        return CommonResult.success(oauth2TokenDto);
//    }

    @GetMapping("/test")
    public String test() {
        return "hello world";
    }
}
