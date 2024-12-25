package com.guigu.ssyx.model.vo.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2024/12/25 14:26
 */
@Data
public class RegionVo {

    @ApiModelProperty(value = "开通区域")
    private Long regionId;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

}
