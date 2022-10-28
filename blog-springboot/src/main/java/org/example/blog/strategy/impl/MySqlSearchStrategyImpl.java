package org.example.blog.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.blog.dao.ArticleDao;
import org.example.blog.dto.ArticleSearchDTO;
import org.example.blog.entity.Article;
import org.example.blog.strategy.SearchStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.example.blog.constant.CommonConst.*;
import static org.example.blog.enums.ArticleStatusEnum.PUBLIC;

/**
 * @Description mysql搜索策略
 * @Author dongp
 * @Date 2022/10/26 0026 18:44
 */
@RequiredArgsConstructor
@Service("mySqlSearchStrategyImpl")
public class MySqlSearchStrategyImpl implements SearchStrategy {

    private final ArticleDao articleDao;

    @Override
    public List<ArticleSearchDTO> searchArticle(String keywords) {
        // 判空
        if (StrUtil.isBlank(keywords)) {
            return new ArrayList<>();
        }
        // 搜索文章
        List<Article> articleList = articleDao.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .and(i -> i.like(Article::getArticleTitle, keywords)
                        .or()
                        .like(Article::getArticleContent, keywords)));
        // 高亮处理
        return articleList.stream()
                .map(item -> {
                    // 获取关键词第一次出现的位置
                    String articleContent = item.getArticleContent();
                    int index = item.getArticleContent().indexOf(keywords);
                    if (index != -1) {
                        // 获取关键词前面的文字
                        int preIndex = index > 25 ? index - 25 : 0;
                        String preText = item.getArticleContent().substring(preIndex, index);
                        // 获取关键词到后面的
                        int last = index + keywords.length();
                        int postLength = item.getArticleContent().length() - last;
                        int postIndex = postLength > 175 ? last + 175 : last + postLength;
                        String postText = item.getArticleContent().substring(index, postIndex);
                        // 文章内容高亮
                        articleContent = (preText + postText).replaceAll(keywords, PRE_TAG + keywords + POST_TAG);
                    }
                    // 文章标题高亮
                    String articleTitle = item.getArticleTitle().replaceAll(keywords, PRE_TAG + keywords + POST_TAG);
                    return ArticleSearchDTO.builder()
                            .id
                })
                .co;
    }
}
