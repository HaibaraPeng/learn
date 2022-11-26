package com.roc.shop.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roc.shop.bean.model.Sku;

import java.util.List;

/**
 * @Description SkuService
 * @Author roc
 * @Date 2022/11/26 下午3:46
 */
public interface SkuService extends IService<Sku> {

    /**
     * 根据商品id获取商品中的sku列表（将会被缓存起来）
     * @param prodId 商品id
     * @return sku列表
     */
    List<Sku> listByProdId(Long prodId);
}
