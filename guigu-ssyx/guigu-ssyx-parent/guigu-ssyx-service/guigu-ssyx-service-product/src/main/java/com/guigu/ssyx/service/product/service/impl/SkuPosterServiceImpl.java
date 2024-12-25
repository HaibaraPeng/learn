package com.guigu.ssyx.service.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.ssyx.model.entity.product.SkuPoster;
import com.guigu.ssyx.service.product.mapper.SkuPosterMapper;
import com.guigu.ssyx.service.product.service.SkuPosterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 15:25
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {

    //根据id查询商品海报列表
    @Override
    public List<SkuPoster> getPosterListBySkuId(Long id) {
        LambdaQueryWrapper<SkuPoster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPoster::getSkuId,id);
        return baseMapper.selectList(wrapper);
    }
}
