package com.roc.shop.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roc.shop.bean.model.Basket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description BasketMapper
 * @Author roc
 * @Date 2022/11/26 下午4:26
 */
public interface BasketMapper extends BaseMapper<Basket> {

    List<String> listUserIdByProdId(@Param("prodId")Long prodId);
}
