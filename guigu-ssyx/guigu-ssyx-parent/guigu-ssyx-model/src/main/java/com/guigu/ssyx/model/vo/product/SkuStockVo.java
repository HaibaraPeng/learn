package com.guigu.ssyx.model.vo.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Roc
 * @Date 2024/12/25 15:13
 */
@Data
public class SkuStockVo implements Serializable {

    @ApiModelProperty(value = "skuId")
    private Long skuId;

    @ApiModelProperty(value = "sku类型")
    private Integer skuType;

    @ApiModelProperty(value = "更新的库存数量")
    private Integer stockNum;

}
