package com.guli.mall.cart.to;

import lombok.Data;

/**
 * @Author Roc
 * @Date 2025/01/05 13:55
 */
@Data
public class UserInfoTo {

    private Long userId;

    private String userKey;

    /**
     * 是否临时用户
     */
    private Boolean tempUser = false;

}
