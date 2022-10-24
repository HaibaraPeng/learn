package org.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 访问量
 * @Author roc
 * @Date 2022/10/24 下午10:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UniqueViewDTO {

    /**
     * 日期
     */
    private String day;

    /**
     * 访问量
     */
    private Integer viewsCount;
}
