package com.guigu.ssyx.model.vo.product;

import com.guigu.ssyx.model.entity.product.SkuAttrValue;
import com.guigu.ssyx.model.entity.product.SkuImage;
import com.guigu.ssyx.model.entity.product.SkuInfo;
import com.guigu.ssyx.model.entity.product.SkuPoster;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 15:11
 */
@Data
public class SkuInfoVo extends SkuInfo {

    @ApiModelProperty(value = "海报列表")
    private List<SkuPoster> skuPosterList;

    @ApiModelProperty(value = "属性值")
    private List<SkuAttrValue> skuAttrValueList;

    @ApiModelProperty(value = "图片")
    private List<SkuImage> skuImagesList;

}
