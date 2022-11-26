package com.roc.shop.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roc.shop.bean.model.Basket;

import java.util.List;

/**
 * @Description BasketService
 * @Author roc
 * @Date 2022/11/26 下午4:25
 */
public interface BasketService extends IService<Basket> {

    void removeShopCartItemsCacheByUserId(String userId);

    /**
     * 获取购物车中拥有某件商品的用户，用于清除该用户购物车的缓存
     * @param prodId 商品id
     * @return 用户id
     */
    List<String> listUserIdByProdId(Long prodId);
}
