package com.roc.shop.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.bean.model.ProdTag;
import com.roc.shop.service.dao.ProdTagMapper;
import com.roc.shop.service.service.ProdTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description ProdTagServiceImpl
 * @Author roc
 * @Date 2022/11/26 下午4:07
 */
@RequiredArgsConstructor
@Service
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag> implements ProdTagService {

    private final ProdTagMapper prodTagMapper;

    @Override
    @Cacheable(cacheNames = "prodTag", key = "'prodTag'")
    public List<ProdTag> listProdTag() {
        return prodTagMapper.selectList(new LambdaQueryWrapper<ProdTag>()
                .eq(ProdTag::getStatus, 1)
                .orderByDesc(ProdTag::getSeq));
    }
}
