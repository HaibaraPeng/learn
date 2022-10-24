package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.Article;
import org.springframework.stereotype.Repository;

/**
 * @Description 文章
 * @Author dongp
 * @Date 2022/10/24 0024 14:43
 */
@Repository
public interface ArticleDao extends BaseMapper<Article> {
}
