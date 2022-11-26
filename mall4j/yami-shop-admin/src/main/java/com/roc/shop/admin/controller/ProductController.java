package com.roc.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.roc.shop.bean.model.Sku;
import com.roc.shop.common.exception.YamiShopBindException;
import com.roc.shop.common.util.PageParam;
import com.roc.shop.bean.model.Product;
import com.roc.shop.bean.param.ProductParam;
import com.roc.shop.security.admin.util.SecurityUtils;
import com.roc.shop.service.service.ProdTagReferenceService;
import com.roc.shop.service.service.ProductService;
import com.roc.shop.service.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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
    private final SkuService skuService;
    private final ProdTagReferenceService prodTagReferenceService;

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

    /**
     * 获取信息
     */
    @GetMapping("/info/{prodId}")
    @PreAuthorize("@pms.hasPermission('prod:prod:info')")
    public ResponseEntity<Product> info(@PathVariable("prodId") Long prodId) {
        Product prod = productService.getProductByProdId(prodId);
        if (!Objects.equals(prod.getShopId(), SecurityUtils.getSysUser().getShopId())) {
            throw new YamiShopBindException("没有权限获取该商品规格信息");
        }
        List<Sku> skuList = skuService.listByProdId(prodId);
        prod.setSkuList(skuList);

        //获取分组标签
        List<Long> listTagId = prodTagReferenceService.listTagIdByProdId(prodId);
        prod.setTagList(listTagId);
        return ResponseEntity.ok(prod);
    }
}
