package org.example.constant;

/**
 * @Author Roc
 * @Date 2025/2/8 17:45
 */
public class RedisConstants {

    /**
     * jwk set缓存前缀
     */
    public static final String AUTHORIZATION_JWS_PREFIX_KEY = "authorization_jws";


    /**
     * 认证信息存储前缀
     */
    public static final String SECURITY_CONTEXT_PREFIX_KEY = "security_context:";

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

    /**
     * 默认过期时间，默认五分钟
     */
    public static final long DEFAULT_TIMEOUT_SECONDS = 60L * 5;
}
