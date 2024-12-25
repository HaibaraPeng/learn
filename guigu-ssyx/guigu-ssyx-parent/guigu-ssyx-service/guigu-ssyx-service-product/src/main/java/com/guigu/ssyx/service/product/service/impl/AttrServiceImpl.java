package com.guigu.ssyx.service.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.ssyx.model.entity.product.Attr;
import com.guigu.ssyx.service.product.mapper.AttrMapper;
import com.guigu.ssyx.service.product.service.AttrService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 14:52
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    //根据平台属性分组id查询
    @Override
    public List<Attr> getAttrListByGroupId(Long groupId) {
        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attr::getAttrGroupId, groupId);
        List<Attr> list = baseMapper.selectList(wrapper);
        return list;
    }
}
