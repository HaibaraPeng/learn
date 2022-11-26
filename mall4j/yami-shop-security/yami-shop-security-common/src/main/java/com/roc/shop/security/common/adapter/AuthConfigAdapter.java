package com.roc.shop.security.common.adapter;

import java.util.List;

/**
 * @Description 实现该接口之后，修改需要授权登陆的路径，不需要授权登陆的路径
 * @Author roc
 * @Date 2022/11/24 下午10:01
 */
public interface AuthConfigAdapter {
    /**
     * 也许需要登录才可用的url
     */
    String MAYBE_AUTH_URI = "/**/ma/**";

    /**
     * 需要授权登陆的路径
     * @return 需要授权登陆的路径列表
     */
    List<String> pathPatterns();

    /**
     * 不需要授权登陆的路径
     * @return 不需要授权登陆的路径列表
     */
    List<String> excludePathPatterns();

}
