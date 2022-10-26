package org.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.blog.dto.ArticleHomeDTO;
import org.example.blog.entity.Article;

import java.util.List;

/**
 * @Description 文章服务
 * @Author dongp
 * @Date 2022/10/26 0026 14:57
 */
public interface ArticleService extends IService<Article> {

    /**
     * 查询首页文章
     *
     * @return 文章列表
     */
    List<ArticleHomeDTO> listArticles();
}
