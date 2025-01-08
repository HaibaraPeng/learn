package com.guigu.ssyx.service.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guigu.ssyx.model.enums.user.User;
import com.guigu.ssyx.model.vo.user.LeaderAddressVo;
import com.guigu.ssyx.model.vo.user.UserLoginVo;

/**
 * @Author Roc
 * @Date 2025/1/8 18:12
 */
public interface UserService extends IService<User> {
    //// 判断是否是第一次使用微信授权登录：如何判断？openId
    User getUserByOpenId(String openid);

    //5 根据userId查询提货点和团长信息
    LeaderAddressVo getLeaderAddressByUserId(Long userId);

    //7 获取当前登录用户信息，
    UserLoginVo getUserLoginVo(Long id);
}
