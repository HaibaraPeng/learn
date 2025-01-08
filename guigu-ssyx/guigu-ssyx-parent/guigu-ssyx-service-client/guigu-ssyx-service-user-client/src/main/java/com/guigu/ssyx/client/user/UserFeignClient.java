package com.guigu.ssyx.client.user;

import com.guigu.ssyx.model.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Roc
 * @Date 2025/1/8 17:50
 */
@FeignClient("service-user")
public interface UserFeignClient {

    //根据userId查询提货点和团长信息
    @GetMapping("/api/user/leader/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId);
}
