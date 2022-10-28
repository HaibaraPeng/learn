package org.example.blog.strategy;

import org.example.blog.dto.ArticleSearchDTO;

import java.util.List;

/**
 * @Description 搜索策略
 * @Author dongp
 * @Date 2022/10/26 0026 18:11
 */
public interface SearchStrategy {

    /**
     * 搜索文章
     *
     * @param keywords 关键字
     * @return {@link List <ArticleSearchDTO>} 文章列表
     */
    List<ArticleSearchDTO> searchArticle(String keywords);
}
