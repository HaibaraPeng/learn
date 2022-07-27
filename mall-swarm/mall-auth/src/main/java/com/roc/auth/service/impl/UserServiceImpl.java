package com.roc.auth.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.roc.auth.constant.MessageConstant;
import com.roc.auth.domain.SecurityUser;
import com.roc.common.constant.AuthConstant;
import com.roc.common.domain.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理业务类
 * Created by macro on 2020/6/19.
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserDetailsService {

    //    @Autowired
//    private UmsAdminService adminService;
//    @Autowired
//    private UmsMemberService memberService;
    private final HttpServletRequest request;
    private final PasswordEncoder passwordEncoder;

    private List<UserDto> userList;

    @PostConstruct
    public void initData() {
        String password = passwordEncoder.encode("123456");
        userList = new ArrayList<>();
        userList.add(new UserDto(1L, "macro", password, 1, "", CollUtil.toList("ADMIN")));
        userList.add(new UserDto(2L, "andy", password, 1, "", CollUtil.toList("TEST")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = request.getParameter("client_id");
        UserDto userDto;
//        if (AuthConstant.ADMIN_CLIENT_ID.equals(clientId)) {
//            userDto = adminService.loadUserByUsername(username);
//        } else {
//            userDto = memberService.loadUserByUsername(username);
//        }
//        if (userDto == null) {
//            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
//        }
        List<UserDto> findUserList = userList.stream().filter(item -> item.getUsername().equals(username)).collect(Collectors.toList());
        if (CollUtil.isEmpty(findUserList)) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        userDto = findUserList.get(0);
        userDto.setClientId(clientId);
        SecurityUser securityUser = new SecurityUser(userDto);
        if (!securityUser.isEnabled()) {
            throw new DisabledException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.CREDENTIALS_EXPIRED);
        }
        return securityUser;
    }

}
