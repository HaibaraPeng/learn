package org.example.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.example.blog.dto.ArticleHomeDTO;
import org.example.blog.service.ArticleService;
import org.example.blog.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 文章控制器
 * @Author dongp
 * @Date 2022/10/26 0026 14:50
 */
@Api(tags = "文章模块")
@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 查看首页文章
     *
     * @return {@link Result<ArticleHomeDTO>} 首页文章列表
     */
    @ApiOperation(value = "查看首页文章")
    @GetMapping("/articles")
    public Result<List<ArticleHomeDTO>> listArticles() {
        return Result.ok(articleService.listArticles());
    }
}
