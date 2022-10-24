package org.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 文章排行
 * @Author roc
 * @Date 2022/10/24 下午10:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRankDTO {

    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 浏览量
     */
    private Integer viewsCount;
}
