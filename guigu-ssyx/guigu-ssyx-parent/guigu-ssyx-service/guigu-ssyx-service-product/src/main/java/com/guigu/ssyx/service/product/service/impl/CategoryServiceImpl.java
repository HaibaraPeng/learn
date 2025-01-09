package com.guigu.ssyx.service.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.ssyx.model.entity.product.Category;
import com.guigu.ssyx.model.vo.product.CategoryQueryVo;
import com.guigu.ssyx.service.product.mapper.CategoryMapper;
import com.guigu.ssyx.service.product.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author Roc
 * @Date 2024/12/25 15:17
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    //商品分类列表
    @Override
    public IPage<Category> selectPageCategory(Page<Category> pageParam,
                                              CategoryQueryVo categoryQueryVo) {
        String name = categoryQueryVo.getName();
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like(Category::getName, name);
        }
        IPage<Category> categoryPage = baseMapper.selectPage(pageParam, wrapper);
        return categoryPage;
    }
}
