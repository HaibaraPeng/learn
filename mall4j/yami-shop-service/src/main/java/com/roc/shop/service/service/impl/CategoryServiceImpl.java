package com.roc.shop.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.bean.model.Category;
import com.roc.shop.service.dao.CategoryMapper;
import com.roc.shop.service.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @Description CategoryServiceImpl
 * @Author roc
 * @Date 2022/11/26 下午3:39
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
