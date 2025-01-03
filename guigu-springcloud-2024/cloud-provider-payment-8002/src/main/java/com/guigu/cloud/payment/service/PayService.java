package com.guigu.cloud.payment.service;

import com.guigu.cloud.payment.entities.Pay;

import java.util.List;

/**
 * @Author Roc
 * @Date 2024/12/24 22:25
 */
public interface PayService {
    public int add(Pay pay);
    public int delete(Integer id);
    public int update(Pay pay);

    public Pay getById(Integer id);

    public List<Pay> getAll();

}
