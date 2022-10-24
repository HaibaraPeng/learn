package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.WebsiteConfig;
import org.springframework.stereotype.Repository;

/**
 * @Description 网站配置
 * @Author dongp
 * @Date 2022/10/24 0024 17:07
 */
@Repository
public interface WebsiteConfigDao extends BaseMapper<WebsiteConfig> {
}
