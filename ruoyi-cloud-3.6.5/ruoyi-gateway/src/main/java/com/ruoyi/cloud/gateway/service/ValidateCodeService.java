package com.ruoyi.cloud.gateway.service;

import com.ruoyi.cloud.common.core.exception.CaptchaException;
import com.ruoyi.cloud.common.core.web.domain.AjaxResult;

import java.io.IOException;

/**
 * @Author Roc
 * @Date 2025/01/07 22:04
 */
public interface ValidateCodeService {
    /**
     * 生成验证码
     */
    public AjaxResult createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;
}
