package com.ruoyi.cloud.modules.system.service.impl;

import com.ruoyi.cloud.api.system.domain.SysLogininfor;
import com.ruoyi.cloud.modules.system.mapper.SysLogininforMapper;
import com.ruoyi.cloud.modules.system.service.ISysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Roc
 * @Date 2025/01/12 17:34
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService {

    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public int insertLogininfor(SysLogininfor logininfor) {
        return logininforMapper.insertLogininfor(logininfor);
    }

//    /**
//     * 查询系统登录日志集合
//     *
//     * @param logininfor 访问日志对象
//     * @return 登录记录集合
//     */
//    @Override
//    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
//        return logininforMapper.selectLogininforList(logininfor);
//    }
//
//    /**
//     * 批量删除系统登录日志
//     *
//     * @param infoIds 需要删除的登录日志ID
//     * @return 结果
//     */
//    @Override
//    public int deleteLogininforByIds(Long[] infoIds) {
//        return logininforMapper.deleteLogininforByIds(infoIds);
//    }
//
//    /**
//     * 清空系统登录日志
//     */
//    @Override
//    public void cleanLogininfor() {
//        logininforMapper.cleanLogininfor();
//    }
}
