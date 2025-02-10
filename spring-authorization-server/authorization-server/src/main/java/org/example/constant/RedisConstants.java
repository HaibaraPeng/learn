package org.example.constant;

/**
 * @Author Roc
 * @Date 2025/2/8 17:45
 */
public class RedisConstants {

    /**
     * 短信验证码前缀
     */
    public static final String SMS_CAPTCHA_PREFIX_KEY = "mobile_phone:";

    /**
     * 图形验证码前缀
     */
    public static final String IMAGE_CAPTCHA_PREFIX_KEY = "image_captcha:";

    /**
     * 验证码过期时间，默认五分钟
     */
    public static final long CAPTCHA_TIMEOUT_SECONDS = 60L * 5;
}
