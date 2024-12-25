package com.guigu.ssyx.service.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.product.SkuAttrValue;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 15:15
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    //根据id查询商品属性信息列表
    List<SkuAttrValue> getAttrValueListBySkuId(Long id);
}
