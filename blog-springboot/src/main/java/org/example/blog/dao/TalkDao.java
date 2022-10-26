package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.Talk;
import org.springframework.stereotype.Repository;

/**
 * @Description 说说
 * @Author dongp
 * @Date 2022/10/26 0026 14:29
 */
@Repository
public interface TalkDao extends BaseMapper<Talk> {
}
