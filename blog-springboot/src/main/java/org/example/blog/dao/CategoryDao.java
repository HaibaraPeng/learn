package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.Category;
import org.springframework.stereotype.Repository;

/**
 * @Description 分类
 * @Author dongp
 * @Date 2022/10/24 0024 15:13
 */
@Repository
public interface CategoryDao extends BaseMapper<Category> {
}
