package com.guigu.ssyx.service.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.entity.sys.RegionWare;
import com.guigu.ssyx.model.vo.sys.RegionWareQueryVo;

/**
 * @Author Roc
 * @Date 2024/12/25 14:26
 */
public interface RegionWareService extends IService<RegionWare> {

    //开通区域列表
    IPage<RegionWare> selectPageRegionWare(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo);

    //添加开通区域
    void saveRegionWare(RegionWare regionWare);

    //取消开通区域
    void updateStatus(Long id, Integer status);
}
