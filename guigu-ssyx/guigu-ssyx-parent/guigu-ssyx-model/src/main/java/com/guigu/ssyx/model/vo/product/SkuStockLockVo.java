package com.guigu.ssyx.model.vo.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Roc
 * @Date 2024/12/25 15:12
 */
@Data
public class SkuStockLockVo implements Serializable {

    @ApiModelProperty(value = "skuId")
    private Long skuId;

    @ApiModelProperty(value = "sku个数")
    private Integer skuNum;

    @ApiModelProperty(value = "是否锁定")
    private Boolean isLock = false;
}
