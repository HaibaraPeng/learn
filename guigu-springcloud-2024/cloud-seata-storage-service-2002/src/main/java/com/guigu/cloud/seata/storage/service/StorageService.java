package com.guigu.cloud.seata.storage.service;

/**
 * @Author Roc
 * @Date 2025/01/04 22:36
 */
public interface StorageService {
    /**
     * 扣减库存
     */
    void decrease(Long productId, Integer count);
}