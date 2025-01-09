package com.guigu.ssyx.service.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.ssyx.model.entity.acl.Admin;
import com.guigu.ssyx.model.vo.acl.AdminQueryVo;
import com.guigu.ssyx.service.acl.mapper.AdminMapper;
import com.guigu.ssyx.service.acl.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author Roc
 * @Date 2024/12/25 11:27
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public IPage<Admin> selectPageUser(Page<Admin> pageParam, AdminQueryVo adminQueryVo) {
        String username = adminQueryVo.getUsername();
        String name = adminQueryVo.getName();
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(username)) {
            wrapper.eq(Admin::getUsername, username);
        }
        if (!StringUtils.isEmpty(name)) {
            wrapper.like(Admin::getName, name);
        }
        IPage<Admin> adminPage = baseMapper.selectPage(pageParam, wrapper);
        return adminPage;
    }
}
