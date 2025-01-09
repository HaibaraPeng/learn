package com.guigu.ssyx.service.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guigu.ssyx.model.entity.sys.RegionWare;
import com.guigu.ssyx.model.vo.sys.RegionWareQueryVo;
import com.guigu.ssyx.service.sys.mapper.RegionWareMapper;
import com.guigu.ssyx.service.sys.service.RegionWareService;
import com.guigu.ssyx.service.util.exception.SsyxException;
import com.guigu.ssyx.service.util.result.ResultCodeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author Roc
 * @Date 2024/12/25 14:27
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {

    //开通区域列表
    @Override
    public IPage<RegionWare> selectPageRegionWare(Page<RegionWare> pageParam,
                                                  RegionWareQueryVo regionWareQueryVo) {
        //1 获取查询条件值
        String keyword = regionWareQueryVo.getKeyword();

        //2 判断条件值是否为空，不为空封装条件
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)) {
            //封装条件
            //根据区域名称 或者 仓库名称查询
            wrapper.like(RegionWare::getRegionName,keyword)
                    .or().like(RegionWare::getWareName,keyword);
        }

        //3 调用方法实现条件分页查询
        IPage<RegionWare> regionWarePage = baseMapper.selectPage(pageParam, wrapper);

        //4 数据返回
        return regionWarePage;
    }

    //添加开通区域
    @Override
    public void saveRegionWare(RegionWare regionWare) {
        //判断区域是否已经开通了
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getRegionId,regionWare.getRegionId());
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0) { //已经存在
            //抛出自定义异常
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        baseMapper.insert(regionWare);
    }

    //取消开通区域
    @Override
    public void updateStatus(Long id, Integer status) {
        RegionWare regionWare = baseMapper.selectById(id);
        regionWare.setStatus(status);
        baseMapper.updateById(regionWare);
    }
}
