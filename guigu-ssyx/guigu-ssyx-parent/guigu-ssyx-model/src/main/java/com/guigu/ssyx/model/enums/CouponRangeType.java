package com.guigu.ssyx.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @Author Roc
 * @Date 2025/1/4 17:39
 */
@Getter
public enum CouponRangeType {
    ALL(1,"通用"),
    SKU(2,"SKU" ),
    CATEGORY(3,"分类");

    @EnumValue
    private Integer code ;
    private String comment ;

    CouponRangeType(Integer code, String comment ){
        this.code=code;
        this.comment=comment;
    }

}
