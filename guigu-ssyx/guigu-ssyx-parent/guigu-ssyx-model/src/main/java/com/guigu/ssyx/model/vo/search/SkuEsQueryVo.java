package com.guigu.ssyx.model.vo.search;

import lombok.Data;

/**
 * @Author Roc
 * @Date 2025/1/4 17:04
 */
@Data
public class SkuEsQueryVo {

    private Long categoryId;;//三级分类id

    private String keyword;//检索的关键字

    private Long wareId;

}
