package org.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.blog.dao.TalkDao;
import org.example.blog.entity.Talk;
import org.example.blog.service.TalkService;
import org.example.blog.util.HTMLUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.blog.constant.RedisPrefixConst.*;
import static org.example.blog.enums.TalkStatusEnum.PUBLIC;

/**
 * @Description 说说服务
 * @Author dongp
 * @Date 2022/10/26 0026 14:28
 */
@RequiredArgsConstructor
@Service
public class TalkServiceImpl extends ServiceImpl<TalkDao, Talk> implements TalkService {

    private final TalkDao talkDao;

    @Override
    public List<String> listHomeTalks() {
        // 查询最新的10条说说
        return talkDao.selectList(new LambdaQueryWrapper<Talk>()
                .eq(Talk::getStatus, PUBLIC.getStatus())
                .orderByDesc(Talk::getIsTop)
                .orderByDesc(Talk::getId)
                .last("limit 10"))
                .stream()
                .map(item -> HTMLUtils.deleteHMTLTag(item.getContent().length() > 200 ?
                        item.getContent().substring(0, 200) : item.getContent()))
                .collect(Collectors.toList());
    }
}
