package com.guigu.ssyx.service.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.acl.Admin;
import com.guigu.ssyx.model.vo.acl.AdminQueryVo;

/**
 * @Author Roc
 * @Date 2024/12/25 11:24
 */
public interface AdminService extends IService<Admin> {

    //1 用户列表
    IPage<Admin> selectPageUser(Page<Admin> pageParam, AdminQueryVo adminQueryVo);
}
