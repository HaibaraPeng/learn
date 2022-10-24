package org.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 分类
 * @Author roc
 * @Date 2022/10/24 下午10:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    /**
     * id
     */
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 分类下的文章数量
     */
    private Integer articleCount;
}
