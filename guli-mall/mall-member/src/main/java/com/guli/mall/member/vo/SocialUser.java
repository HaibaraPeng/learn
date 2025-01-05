package com.guli.mall.member.vo;

import lombok.Data;

/**
 * @Author Roc
 * @Date 2025/01/05 22:35
 */
@Data
public class SocialUser {

    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;

}
