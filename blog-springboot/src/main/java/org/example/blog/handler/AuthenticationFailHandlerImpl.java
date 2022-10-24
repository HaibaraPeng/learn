package org.example.blog.handler;

import com.alibaba.fastjson.JSON;
import org.example.blog.vo.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.example.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * @Description 登录失败处理
 * @Author dongp
 * @Date 2022/10/24 0024 17:40
 */
@Component
public class AuthenticationFailHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write(JSON.toJSONString(Result.fail(exception.getMessage())));
    }
}
