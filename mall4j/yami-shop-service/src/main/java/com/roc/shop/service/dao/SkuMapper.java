package com.roc.shop.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roc.shop.bean.model.Sku;

import java.util.List;

/**
 * @Description SkuMapper
 * @Author roc
 * @Date 2022/11/26 下午3:47
 */
public interface SkuMapper extends BaseMapper<Sku> {

    List<Sku> listByProdId(Long prodId);
}
