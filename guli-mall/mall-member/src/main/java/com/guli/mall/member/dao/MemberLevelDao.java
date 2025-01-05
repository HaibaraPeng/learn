package com.guli.mall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guli.mall.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Roc
 * @Date 2025/01/05 22:40
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {
    MemberLevelEntity getDefaultLevel();
}