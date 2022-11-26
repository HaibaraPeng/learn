package com.roc.shop.security.common.handler;

import cn.hutool.core.util.CharsetUtil;
import com.roc.shop.common.exception.YamiShopBindException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description HttpHandler
 * @Author roc
 * @Date 2022/11/24 下午10:21
 */
@Slf4j
@Component
public class HttpHandler {

    public <T> void printServerResponseToWeb(String str, int status) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.error("requestAttributes is null, can not print to web");
            return;
        }
        HttpServletResponse response = requestAttributes.getResponse();
        if (response == null) {
            log.error("httpServletResponse is null, can not print to web");
            return;
        }
        log.error("response error: " + str);
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.write(str);
        } catch (IOException e) {
            throw new YamiShopBindException("io 异常", e);
        }
    }
}
