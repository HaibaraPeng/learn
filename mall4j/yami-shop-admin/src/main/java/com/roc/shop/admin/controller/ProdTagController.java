package com.roc.shop.admin.controller;

import com.roc.shop.bean.model.ProdTag;
import com.roc.shop.service.service.ProdTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 商品分组
 * @Author roc
 * @Date 2022/11/26 下午4:05
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/prod/prodTag")
public class ProdTagController {

    private final ProdTagService prodTagService;

    @GetMapping("/listTagList")
    public ResponseEntity<List<ProdTag>> listTagList() {
        return ResponseEntity.ok(prodTagService.listProdTag());

    }
}
