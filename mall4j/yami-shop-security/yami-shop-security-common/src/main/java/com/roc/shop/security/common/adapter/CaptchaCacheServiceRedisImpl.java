package com.roc.shop.security.common.adapter;

import com.anji.captcha.service.CaptchaCacheService;
import com.roc.shop.common.util.RedisUtil;

/**
 * @Description 适配验证码在redis的存储
 * @Author roc
 * @Date 2022/11/20 下午5:33
 */
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        RedisUtil.set(key, value, expiresInSeconds);
    }

    @Override
    public boolean exists(String key) {
        return RedisUtil.hasKey(key);
    }

    @Override
    public void delete(String key) {
        RedisUtil.del(key);
    }

    @Override
    public String get(String key) {
        return RedisUtil.get(key);
    }

    @Override
    public String type() {
        return "redis";
    }
}
