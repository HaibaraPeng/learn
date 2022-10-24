package org.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 文章统计
 * @Author roc
 * @Date 2022/10/24 下午10:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleStatisticsDTO {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Integer count;
}
