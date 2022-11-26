package com.roc.shop.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.bean.model.Sku;
import com.roc.shop.service.dao.SkuMapper;
import com.roc.shop.service.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description SkuServiceImpl
 * @Author roc
 * @Date 2022/11/26 下午3:46
 */
@RequiredArgsConstructor
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    private final SkuMapper skuMapper;

    @Override
    @Cacheable(cacheNames = "skuList", key = "#prodId")
    public List<Sku> listByProdId(Long prodId) {
        return skuMapper.listByProdId(prodId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "sku", key = "#skuId"),
            @CacheEvict(cacheNames = "skuList", key = "#prodId")
    })
    public void removeSkuCacheBySkuId(Long skuId,Long prodId) {

    }
}
