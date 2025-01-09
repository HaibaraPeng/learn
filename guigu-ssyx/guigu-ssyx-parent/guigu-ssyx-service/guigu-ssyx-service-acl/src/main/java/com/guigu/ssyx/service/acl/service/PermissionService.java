package com.guigu.ssyx.service.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.acl.Permission;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 11:34
 */
public interface PermissionService extends IService<Permission> {

    //查询所有菜单
    List<Permission> queryAllPermission();

    //递归删除菜单
    void removeChildById(Long id);
}
