package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.Tag;
import org.springframework.stereotype.Repository;

/**
 * @Description 标签
 * @Author dongp
 * @Date 2022/10/24 0024 15:25
 */
@Repository
public interface TagDao extends BaseMapper<Tag> {
}
