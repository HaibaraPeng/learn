package com.guigu.ssyx.model.vo.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2024/12/25 14:26
 */
@Data
public class RegionWareQueryVo {

    @ApiModelProperty(value = "关键字")
    private String keyword;

}
