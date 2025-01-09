package com.guigu.ssyx.service.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guigu.ssyx.model.entity.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Roc
 * @Date 2024/12/25 14:55
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    //解锁库存
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //验证库存
    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //锁定库存:update
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //遍历集合，得到每个对象，减库存
    void minusStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);
}
