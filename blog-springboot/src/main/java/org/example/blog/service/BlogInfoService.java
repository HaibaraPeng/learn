package org.example.blog.service;

import org.example.blog.dto.BlogHomeInfoDTO;

/**
 * @Description 博客信息服务
 * @Author dongp
 * @Date 2022/10/24 0024 14:40
 */
public interface BlogInfoService {

    /**
     * 获取首页数据
     *
     * @return 博客首页信息
     */
    BlogHomeInfoDTO getBlogHomeInfo();
}
