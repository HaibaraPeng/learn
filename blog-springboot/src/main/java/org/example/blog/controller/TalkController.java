package org.example.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.example.blog.service.TalkService;
import org.example.blog.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 说说模块
 * @Author dongp
 * @Date 2022/10/26 0026 14:19
 */
@Api(tags = "说说模块")
@RequiredArgsConstructor
@RestController
public class TalkController {

    private final TalkService talkService;

    /**
     * 查看首页说说
     *
     * @return {@link Result<String>}
     */
    @ApiOperation(value = "查看首页说说")
    @GetMapping("/home/talks")
    public Result<List<String>> listHomeTalks() {
        return Result.ok(talkService.listHomeTalks());
    }
}
