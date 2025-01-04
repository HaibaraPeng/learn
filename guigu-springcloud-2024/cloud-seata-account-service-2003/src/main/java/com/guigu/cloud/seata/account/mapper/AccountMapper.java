package com.guigu.cloud.seata.account.mapper;

import com.guigu.cloud.seata.account.entities.Account;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author Roc
 * @Date 2025/01/04 22:41
 */
public interface AccountMapper extends Mapper<Account> {

    /**
     * @param userId
     * @param money  本次消费金额
     */
    void decrease(@Param("userId") Long userId, @Param("money") Long money);
}
