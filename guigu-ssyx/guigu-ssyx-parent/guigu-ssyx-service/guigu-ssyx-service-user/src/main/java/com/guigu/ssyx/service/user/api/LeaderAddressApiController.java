package com.guigu.ssyx.service.user.api;

import com.guigu.ssyx.model.vo.user.LeaderAddressVo;
import com.guigu.ssyx.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Roc
 * @Date 2025/1/8 18:10
 */
@RestController
@RequestMapping("/api/user/leader")
public class LeaderAddressApiController {

    @Autowired
    private UserService userService;

    //根据userId查询提货点和团长信息
    @GetMapping("/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId) {
        LeaderAddressVo leaderAddress = userService.getLeaderAddressByUserId(userId);
        return leaderAddress;
    }
}
