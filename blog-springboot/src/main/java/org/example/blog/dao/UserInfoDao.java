package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.UserInfo;
import org.springframework.stereotype.Repository;

/**
 * @Description 用户信息
 * @Author roc
 * @Date 2022/10/24 下午10:43
 */
@Repository
public interface UserInfoDao extends BaseMapper<UserInfo> {
}
