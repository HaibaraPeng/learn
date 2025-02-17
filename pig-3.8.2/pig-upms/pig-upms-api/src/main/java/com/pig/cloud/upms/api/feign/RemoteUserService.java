package com.pig.cloud.upms.api.feign;

import com.pig.cloud.common.core.constant.ServiceNameConstants;
import com.pig.cloud.common.core.util.R;
import com.pig.cloud.upms.api.dto.UserDTO;
import com.pig.cloud.upms.api.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author Roc
 * @Date 2025/2/17 15:26
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteUserService {

    /**
     * (未登录状态调用，需要加 @NoToken) 通过用户名查询用户、角色信息
     * @param user 用户查询对象
     * @return R
     */
    // TODO
//    @NoToken
    @GetMapping("/user/info/query")
    R<UserInfo> info(@SpringQueryMap UserDTO user);

}
