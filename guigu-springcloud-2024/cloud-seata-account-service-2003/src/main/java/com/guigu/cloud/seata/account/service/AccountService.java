package com.guigu.cloud.seata.account.service;

import org.apache.ibatis.annotations.Param;

/**
 * @Author Roc
 * @Date 2025/01/04 22:42
 */
public interface AccountService {

    /**
     * 扣减账户余额
     *
     * @param userId 用户id
     * @param money  本次消费金额
     */
    void decrease(@Param("userId") Long userId, @Param("money") Long money);
}
