package com.guigu.ssyx.service.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.product.AttrGroup;
import com.guigu.ssyx.model.vo.product.AttrGroupQueryVo;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 15:04
 */
public interface AttrGroupService extends IService<AttrGroup> {

    //平台属性分组列表
    IPage<AttrGroup> selectPageAttrGroup(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo);

    //查询所有平台属性分组列表
    List<AttrGroup> findAllListAttrGroup();
}
