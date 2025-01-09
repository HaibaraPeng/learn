package com.guigu.ssyx.service.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.product.SkuImage;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 15:16
 */
public interface SkuImageService extends IService<SkuImage> {

    //根据id查询商品图片列表
    List<SkuImage> getImageListBySkuId(Long id);
}
