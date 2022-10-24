package org.example.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.blog.dao.PageDao;
import org.example.blog.entity.Page;
import org.example.blog.service.PageService;
import org.example.blog.service.RedisService;
import org.example.blog.util.BeanCopyUtils;
import org.example.blog.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.example.blog.constant.RedisPrefixConst.PAGE_COVER;

/**
 * @Description 页面服务
 * @Author dongp
 * @Date 2022/10/24 0024 17:11
 */
@RequiredArgsConstructor
@Service
public class PageServiceImpl extends ServiceImpl<PageDao, Page> implements PageService {

    private final RedisService redisService;
    private final PageDao pageDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<PageVO> listPages() {
        List<PageVO> pageVOList;
        // 查找缓存信息,不存在则从mysql读取，更新缓存
        Object pageList = redisService.get(PAGE_COVER);
        if (Objects.nonNull(pageList)) {
            pageVOList = JSON.parseObject(pageList.toString(), List.class);
        } else {
            pageVOList = BeanCopyUtils.copyList(pageDao.selectList(null), PageVO.class);
            redisService.set(PAGE_COVER, JSON.toJSONString(pageVOList));
        }
        return pageVOList;
    }
}
