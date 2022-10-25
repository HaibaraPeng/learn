package org.example.blog.service;

import org.example.blog.dto.BlogBackInfoDTO;
import org.example.blog.dto.BlogHomeInfoDTO;
import org.example.blog.vo.WebsiteConfigVO;

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

    /**
     * 获取后台首页数据
     *
     * @return 博客后台信息
     */
    BlogBackInfoDTO getBlogBackInfo();

    /**
     * 获取网站配置
     *
     * @return {@link WebsiteConfigVO} 网站配置
     */
    WebsiteConfigVO getWebsiteConfig();

    /**
     * 上传访客信息
     */
    void report();
}
