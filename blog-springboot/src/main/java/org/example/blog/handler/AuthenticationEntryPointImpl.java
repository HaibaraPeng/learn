package org.example.blog.handler;

import com.alibaba.fastjson.JSON;
import org.example.blog.enums.StatusCodeEnum;
import org.example.blog.vo.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.example.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * @Description 用户未登录处理
 * @Author dongp
 * @Date 2022/10/24 0024 19:07
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write(JSON.toJSONString(Result.fail(StatusCodeEnum.NO_LOGIN)));
    }
}
