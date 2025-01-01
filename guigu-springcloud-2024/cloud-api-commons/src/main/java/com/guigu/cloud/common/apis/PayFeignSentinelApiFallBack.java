package com.guigu.cloud.common.apis;

import com.guigu.cloud.common.resp.ResultData;
import com.guigu.cloud.common.resp.ReturnCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @Author Roc
 * @Date 2025/01/01 17:29
 */
@Component
public class PayFeignSentinelApiFallBack implements PayFeignSentinelApi {
    @Override
    public ResultData getPayByOrderNo(String orderNo) {
        return ResultData.fail(ReturnCodeEnum.RC500.getCode(), "对方服务宕机或不可用，FallBack服务降级o(╥﹏╥)o");
    }
}
