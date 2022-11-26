package com.roc.shop.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roc.shop.bean.enums.ProdPropRule;
import com.roc.shop.bean.model.ProdProp;
import com.roc.shop.security.admin.util.SecurityUtils;
import com.roc.shop.service.service.ProdPropService;
import com.roc.shop.service.service.ProdPropValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 规格管理
 * @Author roc
 * @Date 2022/11/26 下午3:57
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/prod/spec")
public class SpecController {

    private final ProdPropService prodPropService;
    private final ProdPropValueService prodPropValueService;

    /**
     * 获取所有的规格
     */
    @GetMapping("/list")
    public ResponseEntity<List<ProdProp>> list() {
        List<ProdProp> list = prodPropService.list(
                new LambdaQueryWrapper<ProdProp>()
                        .eq(ProdProp::getRule, ProdPropRule.SPEC.value())
                        .eq(ProdProp::getShopId, SecurityUtils.getSysUser().getShopId()));
        return ResponseEntity.ok(list);
    }
}
