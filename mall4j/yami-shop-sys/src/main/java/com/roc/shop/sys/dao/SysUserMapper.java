package com.roc.shop.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roc.shop.sys.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description SysUserMapper
 * @Author roc
 * @Date 2022/11/21 下午10:32
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户的所有权限
     * @param userId  用户ID
     */
    List<String> queryAllPerms(@Param("userId") Long userId);

    /**
     * 根据用户名获取管理员用户
     * @param username
     * @return
     */
    SysUser selectByUsername(@Param("username") String username);
}
