package com.guigu.ssyx.service.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.ssyx.model.entity.acl.Permission;
import com.guigu.ssyx.service.acl.mapper.PermissionMapper;
import com.guigu.ssyx.service.acl.service.PermissionService;
import com.guigu.ssyx.service.acl.util.PermissionHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 11:35
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Override
    public List<Permission> queryAllPermission() {
        //1 查询所有菜单
        List<Permission> allPermissionList =
                baseMapper.selectList(null);

        //2 转换要求数据格式
        List<Permission> result = PermissionHelper.buildPermission(allPermissionList);
        return result;
    }

    @Override
    public void removeChildById(Long id) {

    }
}
