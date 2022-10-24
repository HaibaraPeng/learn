package org.example.blog.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.blog.dao.UniqueViewDao;
import org.example.blog.dto.UniqueViewDTO;
import org.example.blog.entity.UniqueView;
import org.example.blog.service.RedisService;
import org.example.blog.service.UniqueViewService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description 访问量统计服务
 * @Author roc
 * @Date 2022/10/24 下午10:39
 */
@RequiredArgsConstructor
@Service
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewDao, UniqueView> implements UniqueViewService {

    private final RedisService redisService;
    private final UniqueViewDao uniqueViewDao;

    @Override
    public List<UniqueViewDTO> listUniqueViews() {
        DateTime startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -7));
        DateTime endTime = DateUtil.endOfDay(new Date());
        return uniqueViewDao.listUniqueViews(startTime, endTime);
    }
}
