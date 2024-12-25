package com.guigu.ssyx.service.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.product.Attr;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 14:52
 */
public interface AttrService extends IService<Attr> {

    //根据平台属性分组id查询
    List<Attr> getAttrListByGroupId(Long groupId);
}
