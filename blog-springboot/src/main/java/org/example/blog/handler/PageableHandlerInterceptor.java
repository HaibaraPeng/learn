package org.example.blog.handler;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.blog.util.PageUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.example.blog.constant.CommonConst.*;

/**
 * @Description 分页拦截器
 * @Author dongp
 * @Date 2022/10/26 0026 15:18
 */
public class PageableHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String currentPage = request.getParameter(CURRENT);
        String pageSize = Optional.ofNullable(request.getParameter(SIZE)).orElse(DEFAULT_SIZE);
        if (!StrUtil.isBlank(currentPage)) {
            PageUtils.setCurrentPage(new Page<>(Long.parseLong(currentPage), Long.parseLong(pageSize)));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        PageUtils.remove();
    }
}
