package org.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.blog.dao.ArticleDao;
import org.example.blog.dao.CategoryDao;
import org.example.blog.dao.TagDao;
import org.example.blog.dto.BlogHomeInfoDTO;
import org.example.blog.entity.Article;
import org.example.blog.service.BlogInfoService;
import org.example.blog.vo.PageVO;
import org.example.blog.vo.WebsiteConfigVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.example.blog.constant.CommonConst.*;
import static org.example.blog.enums.ArticleStatusEnum.PUBLIC;

/**
 * @Description 博客信息服务
 * @Author dongp
 * @Date 2022/10/24 0024 14:41
 */
@RequiredArgsConstructor
@Service
public class BlogInfoServiceImpl implements BlogInfoService {

    private final ArticleDao articleDao;
    private final CategoryDao categoryDao;
    private final TagDao tagDao;

    @Override
    public BlogHomeInfoDTO getBlogHomeInfo() {
        // 查询文章数量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, PUBLIC.getStatus())
                .eq(Article::getIsDelete, FALSE));
        // 查询分类数量
        Integer categoryCount = categoryDao.selectCount(null);
        // 查询标签数量
        Integer tagCount = tagDao.selectCount(null);
        // 查询访问量
//        Object count = redisService.get(BLOG_VIEWS_COUNT);
//        String viewsCount = Optional.ofNullable(count).orElse(0).toString();
        // 查询网站配置
//        WebsiteConfigVO websiteConfig = this.getWebsiteConfig();
        // 查询页面图片
//        List<PageVO> pageVOList = pageService.listPages();
        // 封装数据
        return BlogHomeInfoDTO.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .build();
    }
}
