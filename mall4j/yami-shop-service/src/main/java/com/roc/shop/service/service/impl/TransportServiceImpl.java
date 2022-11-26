package com.roc.shop.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.bean.model.Transport;
import com.roc.shop.service.dao.TransportMapper;
import com.roc.shop.service.service.TransportService;
import org.springframework.stereotype.Service;

/**
 * @Description TransportServiceImpl
 * @Author roc
 * @Date 2022/11/26 下午3:30
 */
@Service
public class TransportServiceImpl extends ServiceImpl<TransportMapper, Transport> implements TransportService {
}
