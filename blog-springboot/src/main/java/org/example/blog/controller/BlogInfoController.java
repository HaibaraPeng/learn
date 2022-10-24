package org.example.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.example.blog.dto.BlogHomeInfoDTO;
import org.example.blog.service.BlogInfoService;
import org.example.blog.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 博客信息控制器
 * @Author dongp
 * @Date 2022/10/24 0024 14:16
 */
@Api(tags = "博客信息模块")
@RestController
@RequiredArgsConstructor
public class BlogInfoController {

    private final BlogInfoService blogInfoService;

    /**
     * 查看博客信息
     *
     * @return {@link Result<BlogHomeInfoDTO>} 博客信息
     */
    @ApiOperation(value = "查看博客信息")
    @GetMapping("/")
    public Result<BlogHomeInfoDTO> getBlogHomeInfo() {
        return Result.ok(blogInfoService.getBlogHomeInfo());
    }
}
