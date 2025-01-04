package com.guigu.cloud.seata.storage.mapper;

import com.guigu.cloud.seata.storage.entities.Storage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author Roc
 * @Date 2025/01/04 22:36
 */
public interface StorageMapper extends Mapper<Storage> {

    /**
     * 扣减库存
     */
    void decrease(@Param("productId") Long productId, @Param("count") Integer count);
}
