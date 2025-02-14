package com.pig.cloud.upms.api.feign;

import com.pig.cloud.common.core.constant.ServiceNameConstants;
import com.pig.cloud.common.core.util.R;
import com.pig.cloud.upms.api.entity.SysOauthClientDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Roc
 * @Date 2025/2/14 15:36
 */
@FeignClient(contextId = "remoteClientDetailsService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteClientDetailsService {

    /**
     * 通过clientId 查询客户端信息 (未登录，需要无token 内部调用)
     * @param clientId 用户名
     * @return R
     */
    // TODO
//    @NoToken
    @GetMapping("/client/getClientDetailsById/{clientId}")
    R<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId);

}
