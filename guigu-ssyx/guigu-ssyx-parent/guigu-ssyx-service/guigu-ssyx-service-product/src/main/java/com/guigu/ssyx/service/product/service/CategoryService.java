package com.guigu.ssyx.service.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.product.Category;
import com.guigu.ssyx.model.vo.product.CategoryQueryVo;

/**
 * @Author Roc
 * @Date 2024/12/25 15:14
 */
public interface CategoryService extends IService<Category> {

    //商品分类列表
    IPage<Category> selectPageCategory(Page<Category> pageParam, CategoryQueryVo categoryQueryVo);
}
