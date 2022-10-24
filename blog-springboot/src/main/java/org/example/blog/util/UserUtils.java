package org.example.blog.util;

import org.example.blog.dto.UserDetailDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @Description 用户工具类
 * @Author dongp
 * @Date 2022/10/24 0024 17:34
 */
@Component
public class UserUtils {

    /**
     * 获取当前登录用户
     *
     * @return 用户登录信息
     */
    public static UserDetailDTO getLoginUser() {
        return (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
