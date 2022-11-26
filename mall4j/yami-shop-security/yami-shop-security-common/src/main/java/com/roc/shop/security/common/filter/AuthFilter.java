package com.roc.shop.security.common.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.roc.shop.common.exception.YamiShopBindException;
import com.roc.shop.security.common.adapter.AuthConfigAdapter;
import com.roc.shop.security.common.bo.UserInfoInTokenBO;
import com.roc.shop.security.common.handler.HttpHandler;
import com.roc.shop.security.common.manager.TokenStore;
import com.roc.shop.security.common.util.AuthUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description 授权过滤，只要实现AuthConfigAdapter接口，添加对应路径即可：
 * @Author roc
 * @Date 2022/11/24 下午10:00
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final AuthConfigAdapter authConfigAdapter;
    private final HttpHandler httpHandler;
    private final TokenStore tokenStore;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String requestUri = req.getRequestURI();

        List<String> excludePathPatterns = authConfigAdapter.excludePathPatterns();

        AntPathMatcher pathMatcher = new AntPathMatcher();
        // 如果匹配不需要授权的路径，就不需要校验是否需要授权
        if (CollectionUtil.isNotEmpty(excludePathPatterns)) {
            for (String excludePathPattern : excludePathPatterns) {
                if (pathMatcher.match(excludePathPattern, requestUri)) {
                    chain.doFilter(req, resp);
                    return;
                }
            }
        }

        String accessToken = req.getHeader("Authorization");
        // 也许需要登录，不登陆也能用的uri
        boolean mayAuth = pathMatcher.match(AuthConfigAdapter.MAYBE_AUTH_URI, requestUri);

        UserInfoInTokenBO userInfoInToken = null;

        try {
            // 如果有token，就要获取token
            if (StrUtil.isNotBlank(accessToken)) {
                userInfoInToken = tokenStore.getUserInfoByAccessToken(accessToken, true);
            } else if (!mayAuth) {
                // 返回前端401
                httpHandler.printServerResponseToWeb(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value());
                return;
            }
            // 保存上下文
            AuthUserContext.set(userInfoInToken);

            chain.doFilter(req, resp);
        } catch (Exception e) {
            // 手动捕获下非controller异常
            if (e instanceof YamiShopBindException) {
                httpHandler.printServerResponseToWeb(e.getMessage(), ((YamiShopBindException) e).getHttpStatusCode());
            } else {
                throw e;
            }
        } finally {
            AuthUserContext.clean();
        }
    }
}
