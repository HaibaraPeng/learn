package com.roc.shop.security.common.manager;

import cn.hutool.core.util.StrUtil;
import com.roc.shop.common.exception.YamiShopBindException;
import com.roc.shop.common.util.IPHelper;
import com.roc.shop.common.util.RedisUtil;
import com.roc.shop.security.common.enums.SysTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Description PasswordCheckManager
 * @Author roc
 * @Date 2022/11/22 下午10:05
 */
@RequiredArgsConstructor
@Component
public class PasswordCheckManager {

    private final PasswordEncoder passwordEncoder;

    /**
     * 半小时内最多错误10次
     */
    private static final int TIMES_CHECK_INPUT_PASSWORD_NUM = 10;

    /**
     * 检查用户输入错误的验证码次数
     */
    private static final String CHECK_VALID_CODE_NUM_PREFIX = "checkUserInputErrorPassword_";

    public void checkPassword(SysTypeEnum sysTypeEnum, String userNameOrMobile, String rawPassword, String encodePassword) {
        String checkPrefix = sysTypeEnum.value() + CHECK_VALID_CODE_NUM_PREFIX + IPHelper.getIpAddr();

        int count = 0;
        if (RedisUtil.hasKey(checkPrefix + userNameOrMobile)) {
            count = RedisUtil.get(checkPrefix + userNameOrMobile);
        }
        if (count > TIMES_CHECK_INPUT_PASSWORD_NUM) {
            throw new YamiShopBindException("半小时内密码输入错误十次，已限制登录30分钟");
        }
        // 半小时后失效
        RedisUtil.set(checkPrefix + userNameOrMobile, count, 1800);
        // 密码不正确
        if (StrUtil.isBlank(encodePassword) || !passwordEncoder.matches(rawPassword, encodePassword)) {
            count++;
            // 半小时后失效
            RedisUtil.set(checkPrefix + userNameOrMobile, count, 1800);
            throw new YamiShopBindException("账号或密码不正确");
        }
    }
}
