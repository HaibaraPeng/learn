package com.guigu.ssyx.service.util.auth;

import com.guigu.ssyx.common.util.JwtHelper;
import com.guigu.ssyx.model.vo.user.UserLoginVo;
import com.guigu.ssyx.service.util.constant.RedisConst;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Roc
 * @Date 2025/1/7 16:51
 */
public class UserLoginInterceptor implements HandlerInterceptor {

    private RedisTemplate redisTemplate;

    public UserLoginInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        this.getUserLoginVo(request);
        return true;
    }

    private void getUserLoginVo(HttpServletRequest request) {
        //从请求头获取token
        String token = request.getHeader("token");

        //判断token不为空
        if (!StringUtils.isEmpty(token)) {
            //从token获取userId
            Long userId = 1L;
            //根据userId到Redis获取用户信息
            UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue()
                    .get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);
            //获取数据放到ThreadLocal里面
            if (userLoginVo != null) {
                AuthContextHolder.setUserId(userLoginVo.getUserId());
                AuthContextHolder.setWareId(userLoginVo.getWareId());
                AuthContextHolder.setUserLoginVo(userLoginVo);
            } else {
                AuthContextHolder.setUserId(userId);
            }
        }
    }
}
