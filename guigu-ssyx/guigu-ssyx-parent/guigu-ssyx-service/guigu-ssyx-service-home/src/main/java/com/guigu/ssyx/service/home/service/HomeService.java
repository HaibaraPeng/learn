package com.guigu.ssyx.service.home.service;

import java.util.Map;

/**
 * @Author Roc
 * @Date 2025/1/8 16:48
 */
public interface HomeService {

    //首页数据显示接口
    Map<String, Object> homeData(Long userId);
}