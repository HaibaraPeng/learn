package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.UserAuth;
import org.springframework.stereotype.Repository;

/**
 * @Description 用户账号
 * @Author dongp
 * @Date 2022/10/24 0024 17:32
 */
@Repository
public interface UserAuthDao extends BaseMapper<UserAuth> {
}
