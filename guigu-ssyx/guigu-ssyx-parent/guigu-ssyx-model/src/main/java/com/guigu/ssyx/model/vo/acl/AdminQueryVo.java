package com.guigu.ssyx.model.vo.acl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Roc
 * @Date 2024/12/25 11:25
 */
@Data
@ApiModel(description = "用户查询实体")
public class AdminQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String name;

}
