package com.roc.shop.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roc.shop.bean.model.Sku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description SkuMapper
 * @Author roc
 * @Date 2022/11/26 下午3:47
 */
public interface SkuMapper extends BaseMapper<Sku> {

    /**
     * 批量插入sku
     * @param prodId 商品id
     * @param skus sku列表
     */
    void insertBatch(@Param("prodId") Long prodId, @Param("skuList") List<Sku> skuList);

    List<Sku> listByProdId(Long prodId);

    void deleteByProdId(@Param("prodId") Long prodId);
}
