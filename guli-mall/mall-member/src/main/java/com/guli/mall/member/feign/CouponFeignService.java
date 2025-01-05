package com.guli.mall.member.feign;

import com.guli.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Roc
 * @Date 2025/01/05 22:33
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 远程调用 coupon 测试
     *
     * @return
     */
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
