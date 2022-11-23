package com.roc.shop.security.common.manager;

import cn.hutool.crypto.symmetric.AES;
import com.roc.shop.common.exception.YamiShopBindException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Description PasswordManager
 * @Author roc
 * @Date 2022/11/22 下午9:56
 */
@Slf4j
@Component
public class PasswordManager {

    /**
     * 用于aes签名的key 16位
     */
    @Value("${auth.password.signKey:-mall4j-password}")
    public String passwordSignKey;

    public String decryptPassword(String data) {
        AES aes = new AES(passwordSignKey.getBytes(StandardCharsets.UTF_8));
        String decryptStr;
        String decryptPassword;
        try {
            decryptStr = aes.decryptStr(data);
            decryptPassword = decryptStr.substring(13);
        } catch (Exception e) {
            log.error("Exception:", e);
            throw new YamiShopBindException("AES解密错误", e);
        }
        return decryptPassword;
    }
}
