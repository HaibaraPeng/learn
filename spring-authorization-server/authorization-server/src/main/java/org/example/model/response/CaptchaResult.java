package org.example.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2025/2/8 17:46
 */
@Data
@AllArgsConstructor
public class CaptchaResult {

    /**
     * 验证码id
     */
    private String captchaId;

    /**
     * 验证码的值
     */
    private String code;

    /**
     * 图片验证码的base64值
     */
    private String imageData;
}
