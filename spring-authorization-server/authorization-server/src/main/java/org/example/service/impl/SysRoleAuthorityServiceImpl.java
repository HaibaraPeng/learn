package org.example.service.impl;

import org.example.entity.SysRoleAuthority;
import org.example.mapper.SysRoleAuthorityMapper;
import org.example.service.ISysRoleAuthorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色菜单多对多关联表 服务实现类
 * </p>
 *
 * @author Roc
 * @since 2025-02-08
 */
@Service
public class SysRoleAuthorityServiceImpl extends ServiceImpl<SysRoleAuthorityMapper, SysRoleAuthority> implements ISysRoleAuthorityService {

}
