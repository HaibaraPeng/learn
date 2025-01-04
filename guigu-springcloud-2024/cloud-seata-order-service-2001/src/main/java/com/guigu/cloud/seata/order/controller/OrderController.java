package com.guigu.cloud.seata.order.controller;

import com.guigu.cloud.common.resp.ResultData;
import com.guigu.cloud.seata.order.entities.Order;
import com.guigu.cloud.seata.order.serivce.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Roc
 * @Date 2025/01/04 22:25
 */
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     */
    @GetMapping("/order/create")
    public ResultData create(Order order) {
        orderService.create(order);
        return ResultData.success(order);
    }
}
