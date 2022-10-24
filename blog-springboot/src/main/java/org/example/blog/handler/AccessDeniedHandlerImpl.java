package org.example.blog.handler;

import com.alibaba.fastjson.JSON;
import org.example.blog.vo.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.example.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * @Description 用户权限处理
 * @Author dongp
 * @Date 2022/10/24 0024 19:10
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write(JSON.toJSONString(Result.fail("权限不足")));
    }
}
