package com.roc.shop.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.sys.dao.SysUserMapper;
import com.roc.shop.sys.model.SysUser;
import com.roc.shop.sys.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description SysUserServiceImpl
 * @Author roc
 * @Date 2022/11/21 下午10:32
 */
@RequiredArgsConstructor
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser getByUserName(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public List<String> queryAllPerms(Long userId) {
        return sysUserMapper.queryAllPerms(userId);
    }
}
