package com.pig.cloud.common.security.service;

import com.pig.cloud.common.core.constant.CacheConstants;
import com.pig.cloud.common.core.util.R;
import com.pig.cloud.upms.api.dto.UserDTO;
import com.pig.cloud.upms.api.dto.UserInfo;
import com.pig.cloud.upms.api.feign.RemoteUserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Author Roc
 * @Date 2025/2/17 15:25
 */
@Slf4j
@Primary
@RequiredArgsConstructor
public class PigUserDetailsServiceImpl implements PigUserDetailsService {

    private final RemoteUserService remoteUserService;

    private final CacheManager cacheManager;

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @return
     */
    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
        if (cache != null && cache.get(username) != null) {
            return (PigUser) cache.get(username).get();
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        R<UserInfo> result = remoteUserService.info(userDTO);
        UserDetails userDetails = getUserDetails(result);
        if (cache != null) {
            cache.put(username, userDetails);
        }
        return userDetails;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
