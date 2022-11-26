package com.roc.shop.service.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.bean.model.ProdTagReference;
import com.roc.shop.bean.model.Product;
import com.roc.shop.bean.model.Sku;
import com.roc.shop.service.dao.ProdTagReferenceMapper;
import com.roc.shop.service.dao.ProductMapper;
import com.roc.shop.service.dao.SkuMapper;
import com.roc.shop.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description ProductServiceImpl
 * @Author roc
 * @Date 2022/11/26 下午3:09
 */
@RequiredArgsConstructor
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;
    private final ProdTagReferenceMapper prodTagReferenceMapper;

    @Override
    public Product getProductByProdId(Long prodId) {
        return productMapper.selectById(prodId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = "product", key = "#product.prodId"),
            @CacheEvict(cacheNames = "skuList", key = "#product.prodId")
    })
    public void updateProduct(Product product, Product dbProduct) {
        productMapper.updateById(product);
        List<Long> dbSkuIds = dbProduct.getSkuList().stream().map(Sku::getSkuId).collect(Collectors.toList());
        // 2019/04/c0244be79909484fb67bc6d5f70cae18.jpg
//        if (!Objects.equals(dbProduct.getPic(), product.getPic()) && StrUtil.isNotBlank(dbProduct.getPic())) {
//            // 删除数据库中的商品图片
//            attachFileService.deleteFile(dbProduct.getPic());
//        }
        // 将所有该商品的sku标记为已删除状态
        skuMapper.deleteByProdId(product.getProdId());

        // 接口传入sku列表
        List<Sku> skuList = product.getSkuList();

        if (CollectionUtil.isEmpty(skuList)) {
            return;
        }

        List<Sku> insertSkuList = new ArrayList<>();
        for (Sku sku : skuList) {
            sku.setIsDelete(0);
            // 如果数据库中原有sku就更新，否者就插入
            if (dbSkuIds.contains(sku.getSkuId())) {
                skuMapper.updateById(sku);
            } else {
                insertSkuList.add(sku);
            }
        }

        // 批量插入sku
        if (CollectionUtil.isNotEmpty(insertSkuList)) {
            skuMapper.insertBatch(product.getProdId(), insertSkuList);
        }

        //更新分组信息
        List<Long> tagList = product.getTagList();
        if (CollectionUtil.isNotEmpty(tagList)) {
            prodTagReferenceMapper.delete(new LambdaQueryWrapper<ProdTagReference>().eq(ProdTagReference::getProdId, product.getProdId()));
            prodTagReferenceMapper.insertBatch(dbProduct.getShopId(), product.getProdId(), tagList);
        }
    }
}
