package com.roc.shop.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roc.shop.bean.model.ProdTagReference;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description ProdTagReferenceMapper
 * @Author roc
 * @Date 2022/11/26 下午3:52
 */
public interface ProdTagReferenceMapper extends BaseMapper<ProdTagReference> {

    void insertBatch(@Param("shopId") Long shopId, @Param("prodId") Long prodId, @Param("tagList") List<Long> tagList);

    List<Long> listTagIdByProdId(@Param("prodId") Long prodId);
}
