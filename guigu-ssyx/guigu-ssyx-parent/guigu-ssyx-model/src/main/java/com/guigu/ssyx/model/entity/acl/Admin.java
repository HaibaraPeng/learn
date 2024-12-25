package com.guigu.ssyx.model.entity.acl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.guigu.ssyx.model.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2024/12/25 11:24
 */
@Data
@ApiModel(description = "用户")
@TableName("admin")
public class Admin extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "昵称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "手机")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "仓库id")
    @TableField("ware_id")
    private Long wareId;

    @ApiModelProperty(value = "角色名称")
    @TableField(exist = false)
    private String roleName;
}
