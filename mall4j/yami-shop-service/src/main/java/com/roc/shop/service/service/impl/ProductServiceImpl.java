package com.roc.shop.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roc.shop.bean.model.Product;
import com.roc.shop.service.dao.ProductMapper;
import com.roc.shop.service.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * @Description ProductServiceImpl
 * @Author roc
 * @Date 2022/11/26 下午3:09
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
}
