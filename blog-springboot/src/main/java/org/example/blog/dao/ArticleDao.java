package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.blog.dto.ArticleHomeDTO;
import org.example.blog.entity.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 文章
 * @Author dongp
 * @Date 2022/10/24 0024 14:43
 */
@Repository
public interface ArticleDao extends BaseMapper<Article> {

    /**
     * 查询首页文章
     *
     * @param current 页码
     * @param size    大小
     * @return 文章列表
     */
    List<ArticleHomeDTO> listArticles(@Param("current") Long current, @Param("size") Long size);
}
