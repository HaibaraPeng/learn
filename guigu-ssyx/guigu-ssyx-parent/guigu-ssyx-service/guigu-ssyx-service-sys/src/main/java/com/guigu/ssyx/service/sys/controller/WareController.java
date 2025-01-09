package com.guigu.ssyx.service.sys.controller;

import com.guigu.ssyx.model.entity.sys.Ware;
import com.guigu.ssyx.service.sys.service.WareService;
import com.guigu.ssyx.service.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/25 14:29
 */
@Api(tags = "仓库接口")
@RestController
@RequestMapping("/admin/sys/ware")
//@CrossOrigin
public class WareController {

    @Autowired
    private WareService wareService;

    //查询所有仓库列表
//    url: `${api_name}/findAllList`,
//    method: 'get'
    @ApiOperation("查询所有仓库列表")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<Ware> list = wareService.list();
        return Result.ok(list);
    }
}
