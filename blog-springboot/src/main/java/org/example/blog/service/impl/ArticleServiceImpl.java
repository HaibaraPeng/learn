package org.example.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.dao.ArticleDao;
import org.example.blog.dto.ArticleHomeDTO;
import org.example.blog.entity.Article;
import org.example.blog.service.ArticleService;
import org.example.blog.util.PageUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description 文章服务
 * @Author dongp
 * @Date 2022/10/26 0026 14:57
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {

    private final ArticleDao articleDao;

    @Override
    public List<ArticleHomeDTO> listArticles() {
        return articleDao.listArticles(PageUtils.getLimitCurrent(), PageUtils.getSize());
    }
}
