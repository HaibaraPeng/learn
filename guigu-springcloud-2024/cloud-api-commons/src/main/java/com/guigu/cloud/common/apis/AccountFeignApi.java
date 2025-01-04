package com.guigu.cloud.common.apis;

import com.guigu.cloud.common.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Roc
 * @Date 2025/01/04 22:07
 */
@FeignClient(value = "seata-account-service")
public interface AccountFeignApi {
    //扣减账户余额
    @PostMapping("/account/decrease")
    ResultData decrease(@RequestParam("userId") Long userId, @RequestParam("money") Long money);
}
