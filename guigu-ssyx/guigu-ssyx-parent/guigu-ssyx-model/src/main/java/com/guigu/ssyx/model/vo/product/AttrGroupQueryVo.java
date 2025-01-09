package com.guigu.ssyx.model.vo.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2024/12/25 15:07
 */
@Data
public class AttrGroupQueryVo {

    @ApiModelProperty(value = "组名")
    private String name;

}
