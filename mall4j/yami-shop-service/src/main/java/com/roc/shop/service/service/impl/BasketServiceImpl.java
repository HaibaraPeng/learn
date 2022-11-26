package com.roc.shop.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.bean.model.Basket;
import com.roc.shop.service.dao.BasketMapper;
import com.roc.shop.service.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description BasketServiceImpl
 * @Author roc
 * @Date 2022/11/26 下午4:26
 */
@RequiredArgsConstructor
@Service
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket> implements BasketService {

    private final BasketMapper basketMapper;

    @Override
    @CacheEvict(cacheNames = "ShopCartItems", key = "#userId")
    public void removeShopCartItemsCacheByUserId(String userId) {

    }

    @Override
    public List<String> listUserIdByProdId(Long prodId) {
        return basketMapper.listUserIdByProdId(prodId);
    }
}
