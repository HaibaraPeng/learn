package org.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.blog.entity.Talk;

import java.util.List;

/**
 * @Description 说说服务
 * @Author dongp
 * @Date 2022/10/26 0026 14:27
 */
public interface TalkService extends IService<Talk> {

    /**
     * 获取首页说说列表
     *
     * @return {@link List <String>} 说说列表
     */
    List<String> listHomeTalks();
}
