package org.example.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.blog.dao.*;
import org.example.blog.dto.BlogBackInfoDTO;
import org.example.blog.dto.BlogHomeInfoDTO;
import org.example.blog.dto.UniqueViewDTO;
import org.example.blog.entity.Article;
import org.example.blog.service.BlogInfoService;
import org.example.blog.service.PageService;
import org.example.blog.service.RedisService;
import org.example.blog.service.UniqueViewService;
import org.example.blog.vo.PageVO;
import org.example.blog.vo.WebsiteConfigVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.blog.constant.CommonConst.*;
import static org.example.blog.constant.RedisPrefixConst.*;
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
    private final WebsiteConfigDao websiteConfigDao;
    private final MessageDao messageDao;
    private final UserInfoDao userInfoDao;
    private final RedisService redisService;
    private final PageService pageService;
    private final UniqueViewService uniqueViewService;
    private final HttpServletRequest request;

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
        Object count = redisService.get(BLOG_VIEWS_COUNT);
        String viewsCount = Optional.ofNullable(count).orElse(0).toString();
        // 查询网站配置
        WebsiteConfigVO websiteConfig = this.getWebsiteConfig();
        // 查询页面图片
        List<PageVO> pageVOList = pageService.listPages();
        // 封装数据
        return BlogHomeInfoDTO.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .viewsCount(viewsCount)
                .websiteConfig(websiteConfig)
                .pageList(pageVOList)
                .build();
    }

    @Override
    public BlogBackInfoDTO getBlogBackInfo() {
        // 查询访问量
        Object count = redisService.get(BLOG_VIEWS_COUNT);
        Integer viewsCount = Integer.parseInt(Optional.ofNullable(count).orElse(0).toString());
        // 查询留言量
        Integer messageCount = messageDao.selectCount(null);
        // 查询用户量
        Integer userCount = userInfoDao.selectCount(null);
        // 查询文章量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, FALSE));
        // 查询一周用户量
        List<UniqueViewDTO> uniqueViewList = uniqueViewService.listUniqueViews();
        // TODO
        return null;
    }

    @Override
    public WebsiteConfigVO getWebsiteConfig() {
        WebsiteConfigVO websiteConfigVO;
        // 获取缓存数据
        Object websiteConfig = redisService.get(WEBSITE_CONFIG);
        if (Objects.nonNull(websiteConfig)) {
            websiteConfigVO = JSON.parseObject(websiteConfig.toString(), WebsiteConfigVO.class);
        } else {
            // 从数据库中加载
            String config = websiteConfigDao.selectById(DEFAULT_CONFIG_ID).getConfig();
            websiteConfigVO = JSON.parseObject(config, WebsiteConfigVO.class);
            redisService.set(WEBSITE_CONFIG, config);
        }
        return websiteConfigVO;
    }
}
