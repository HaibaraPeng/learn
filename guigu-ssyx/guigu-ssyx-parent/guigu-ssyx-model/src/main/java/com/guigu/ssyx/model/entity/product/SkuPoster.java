package com.guigu.ssyx.model.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.guigu.ssyx.model.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2024/12/25 14:50
 */
@Data
@ApiModel(description = "SkuPoster")
@TableName("sku_poster")
public class SkuPoster extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    @TableField("sku_id")
    private Long skuId;

    @ApiModelProperty(value = "文件名称")
    @TableField("img_name")
    private String imgName;

    @ApiModelProperty(value = "文件路径")
    @TableField("img_url")
    private String imgUrl;

}
