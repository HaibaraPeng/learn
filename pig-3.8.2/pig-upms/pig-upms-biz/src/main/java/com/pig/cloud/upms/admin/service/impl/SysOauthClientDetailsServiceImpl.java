package com.pig.cloud.upms.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig.cloud.upms.admin.mapper.SysOauthClientDetailsMapper;
import com.pig.cloud.upms.admin.service.SysOauthClientDetailsService;
import com.pig.cloud.upms.api.entity.SysOauthClientDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Roc
 * @Date 2025/2/14 16:00
 */
@Service
@RequiredArgsConstructor
public class SysOauthClientDetailsServiceImpl extends ServiceImpl<SysOauthClientDetailsMapper, SysOauthClientDetails>
        implements SysOauthClientDetailsService {

//    /**
//     * 根据客户端信息
//     * @param clientDetails
//     * @return
//     */
//    @Override
//    @CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientDetails.clientId")
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean updateClientById(SysOauthClientDetails clientDetails) {
//        this.insertOrUpdate(clientDetails);
//        return Boolean.TRUE;
//    }
//
//    /**
//     * 添加客户端
//     * @param clientDetails
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean saveClient(SysOauthClientDetails clientDetails) {
//        this.insertOrUpdate(clientDetails);
//        return Boolean.TRUE;
//    }
//
//    /**
//     * 插入或更新客户端对象
//     * @param clientDetails
//     * @return
//     */
//    private SysOauthClientDetails insertOrUpdate(SysOauthClientDetails clientDetails) {
//        // 更新数据库
//        saveOrUpdate(clientDetails);
//        return clientDetails;
//    }
//
//    /**
//     * 分页查询客户端信息
//     * @param page
//     * @param query
//     * @return
//     */
//    @Override
//    public Page queryPage(Page page, SysOauthClientDetails query) {
//        return baseMapper.selectPage(page, Wrappers.query(query));
//    }
//
//    @Override
//    @CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, allEntries = true)
//    public R syncClientCache() {
//        return R.ok();
//    }

}
