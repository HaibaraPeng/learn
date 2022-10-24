package org.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 标签
 * @Author roc
 * @Date 2022/10/24 下午10:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {

    /**
     * id
     */
    private Integer id;

    /**
     * 标签名
     */
    private String tagName;
}
