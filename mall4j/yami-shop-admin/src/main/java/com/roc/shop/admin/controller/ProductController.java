package com.roc.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.roc.shop.common.util.PageParam;
import com.roc.shop.bean.model.Product;
import com.roc.shop.bean.param.ProductParam;
import com.roc.shop.security.admin.util.SecurityUtils;
import com.roc.shop.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 商品列表、商品发布controller
 * @Author roc
 * @Date 2022/11/26 上午11:16
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/prod/prod")
public class ProductController {

    private final ProductService productService;

    /**
     * 分页获取商品信息
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('prod:prod:page')")
    public ResponseEntity<IPage<Product>> page(ProductParam product, PageParam<Product> page) {
        IPage<Product> products = productService.page(page, new LambdaQueryWrapper<Product>()
                .like(StrUtil.isNotBlank(product.getProdName()), Product::getProdName, product.getProdName())
                .eq(Product::getShopId, SecurityUtils.getSysUser().getShopId())
                .eq(product.getStatus() != null, Product::getStatus, product.getStatus())
                .orderByDesc(Product::getPutawayTime)
        );
        return ResponseEntity.ok(products);
    }
}
