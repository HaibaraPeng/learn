package com.roc.shop.security.common.enums;

/**
 * @Description 系统类型
 * @Author roc
 * @Date 2022/11/22 下午10:07
 */
public enum SysTypeEnum {

    /**
     * 普通用户系统
     */
    ORDINARY(0),

    /**
     * 后台
     */
    ADMIN(1),
    ;

    private final Integer value;

    public Integer value() {
        return value;
    }

    SysTypeEnum(Integer value) {
        this.value = value;
    }
}
