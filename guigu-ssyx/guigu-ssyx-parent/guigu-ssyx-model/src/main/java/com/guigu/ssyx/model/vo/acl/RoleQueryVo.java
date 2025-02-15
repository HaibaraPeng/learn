package com.guigu.ssyx.model.vo.acl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Roc
 * @Date 2024/12/24 19:03
 */
@Data
@ApiModel(description = "角色查询实体")
public class RoleQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

}
