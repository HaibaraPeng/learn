package com.roc.shop.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roc.shop.bean.model.ProdTag;

import java.util.List;

/**
 * @Description ProdTagService
 * @Author roc
 * @Date 2022/11/26 下午4:06
 */
public interface ProdTagService extends IService<ProdTag> {

    List<ProdTag> listProdTag();
}
