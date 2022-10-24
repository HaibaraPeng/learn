package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.dto.ResourceRoleDTO;
import org.example.blog.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 角色
 * @Author dongp
 * @Date 2022/10/24 0024 18:25
 */
@Repository
public interface RoleDao extends BaseMapper<Role> {

    /**
     * 查询路由角色列表
     *
     * @return 角色标签
     */
    List<ResourceRoleDTO> listResourceRoles();
}
