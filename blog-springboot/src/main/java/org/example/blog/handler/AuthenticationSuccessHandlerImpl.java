package org.example.blog.handler;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.blog.dao.UserAuthDao;
import org.example.blog.dto.UserInfoDTO;
import org.example.blog.entity.UserAuth;
import org.example.blog.util.BeanCopyUtils;
import org.example.blog.util.UserUtils;
import org.example.blog.vo.Result;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.example.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * @Description 登录成功处理
 * @Author dongp
 * @Date 2022/10/24 0024 17:31
 */
@RequiredArgsConstructor
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final UserAuthDao userAuthDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 返回登录信息
        UserInfoDTO userLoginDTO = BeanCopyUtils.copyObject(UserUtils.getLoginUser(), UserInfoDTO.class);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write(JSON.toJSONString(Result.ok(userLoginDTO)));
        // 更新用户ip，最近登录事件
        updateUserInfo();
    }

    /**
     * 更新用户信息
     */
    @Async
    public void updateUserInfo() {
        UserAuth userAuth = UserAuth.builder()
                .id(UserUtils.getLoginUser().getId())
                .ipAddress(UserUtils.getLoginUser().getIpAddress())
                .ipSource(UserUtils.getLoginUser().getIpSource())
                .lastLoginTime(UserUtils.getLoginUser().getLastLoginTime())
                .build();
        userAuthDao.updateById(userAuth);
    }
}
