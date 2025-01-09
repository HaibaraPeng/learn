package com.guigu.ssyx.service.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.sys.Region;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 11:51
 */
public interface RegionService extends IService<Region> {

    //根据区域关键字查询区域列表信息
    List<Region> getRegionByKeyword(String keyword);
}
