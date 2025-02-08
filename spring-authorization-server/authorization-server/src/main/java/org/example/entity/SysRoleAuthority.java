package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色菜单多对多关联表
 * </p>
 *
 * @author Roc
 * @since 2025-02-08
 */
@Getter
@Setter
@TableName("sys_role_authority")
public class SysRoleAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色菜单关联表自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 权限菜单ID
     */
    private Integer authorityId;
}
