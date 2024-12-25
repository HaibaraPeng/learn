package com.guigu.ssyx.service.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.product.SkuPoster;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 15:16
 */
public interface SkuPosterService extends IService<SkuPoster> {

    //根据id查询商品海报列表
    List<SkuPoster> getPosterListBySkuId(Long id);
}
