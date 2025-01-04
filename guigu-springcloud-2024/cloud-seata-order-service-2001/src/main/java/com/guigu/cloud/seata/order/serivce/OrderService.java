package com.guigu.cloud.seata.order.serivce;

import com.guigu.cloud.seata.order.entities.Order;

/**
 * @Author Roc
 * @Date 2025/01/04 22:25
 */
public interface OrderService {
    /**
     * 创建订单
     */
    void create(Order order);
}
