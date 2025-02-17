package com.pig.cloud.upms.api.dto;

import com.pig.cloud.upms.api.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Roc
 * @Date 2025/2/17 15:20
 */
@Data
@Schema(description = "用户信息")
public class UserInfo implements Serializable {

    /**
     * 用户基本信息
     */
    @Schema(description = "用户基本信息")
    private SysUser sysUser;

    /**
     * 权限标识集合
     */
    @Schema(description = "权限标识集合")
    private String[] permissions;

    /**
     * 角色集合
     */
    @Schema(description = "角色标识集合")
    private Long[] roles;

}
