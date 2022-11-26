package com.roc.shop.security.admin.model;

import lombok.Data;

import java.util.Set;

/**
 * @Description 用户详细信息
 * @Author roc
 * @Date 2022/11/24 下午9:41
 */
@Data
public class YamiSysUser {

    /**
     * 用户ID
     */
    private Long userId;

    private boolean enabled;

    private Set<String> authorities;

    private String username;

    private Long shopId;
}
