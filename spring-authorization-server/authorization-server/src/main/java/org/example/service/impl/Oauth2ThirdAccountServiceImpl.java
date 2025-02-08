package org.example.service.impl;

import org.example.entity.Oauth2ThirdAccount;
import org.example.mapper.Oauth2ThirdAccountMapper;
import org.example.service.IOauth2ThirdAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 三方登录账户信息表 服务实现类
 * </p>
 *
 * @author Roc
 * @since 2025-02-08
 */
@Service
public class Oauth2ThirdAccountServiceImpl extends ServiceImpl<Oauth2ThirdAccountMapper, Oauth2ThirdAccount> implements IOauth2ThirdAccountService {

}
