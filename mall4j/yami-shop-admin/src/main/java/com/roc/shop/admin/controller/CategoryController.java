package com.roc.shop.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roc.shop.bean.model.Category;
import com.roc.shop.security.admin.util.SecurityUtils;
import com.roc.shop.service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 分类管理
 * @Author roc
 * @Date 2022/11/26 下午3:35
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/prod/category")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 所有的
     */
    @GetMapping("/listCategory")
    public ResponseEntity<List<Category>> listCategory(){
        return ResponseEntity.ok(categoryService.list(new LambdaQueryWrapper<Category>()
                .le(Category::getGrade, 2)
                .eq(Category::getShopId, SecurityUtils.getSysUser().getShopId())
                .orderByAsc(Category::getSeq)));
    }
}
