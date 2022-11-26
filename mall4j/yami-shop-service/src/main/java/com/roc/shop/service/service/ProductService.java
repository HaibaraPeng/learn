package com.roc.shop.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roc.shop.bean.model.Product;

/**
 * @Description ProductService
 * @Author roc
 * @Date 2022/11/26 下午3:09
 */
public interface ProductService extends IService<Product> {

    /**
     * 根据商品id获取商品信息
     *
     * @param prodId
     * @return
     */
    Product getProductByProdId(Long prodId);

    /**
     * 更新商品
     *
     * @param product 商品信息
     */
    void updateProduct(Product product, Product dbProduct);
}
