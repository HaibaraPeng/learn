package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.blog.entity.Message;
import org.springframework.stereotype.Repository;

/**
 * @Description 留言
 * @Author roc
 * @Date 2022/10/24 下午10:32
 */
@Repository
public interface MessageDao extends BaseMapper<Message> {
}
