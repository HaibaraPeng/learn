package com.roc.shop.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roc.shop.bean.model.Transport;
import com.roc.shop.security.admin.util.SecurityUtils;
import com.roc.shop.service.service.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description TransportController
 * @Author roc
 * @Date 2022/11/26 下午3:23
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/transport")
public class TransportController {

    private final TransportService transportService;

    /**
     * 获取运费模板列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Transport>> list() {
        Long shopId = SecurityUtils.getSysUser().getShopId();
        List<Transport> list = transportService.list(new LambdaQueryWrapper<Transport>().eq(Transport::getShopId, shopId));
        return ResponseEntity.ok(list);
    }
}
