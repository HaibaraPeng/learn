package com.roc.shop.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roc.shop.bean.model.ProdTagReference;

import java.util.List;

/**
 * @Description 分组标签引用
 * @Author roc
 * @Date 2022/11/26 下午3:50
 */
public interface ProdTagReferenceService extends IService<ProdTagReference> {

    List<Long> listTagIdByProdId(Long prodId);
}
