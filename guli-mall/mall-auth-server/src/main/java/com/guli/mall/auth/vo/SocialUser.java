package com.guli.mall.auth.vo;

import lombok.Data;

/**
 * @Author Roc
 * @Date 2025/01/05 14:53
 */
@Data
public class SocialUser {

    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;

}
