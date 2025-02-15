package com.guigu.ssyx.model.vo.order;

import com.guigu.ssyx.model.entity.activity.CouponInfo;
import com.guigu.ssyx.model.vo.user.LeaderAddressVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Roc
 * @Date 2025/1/4 17:36
 */
@Data
public class OrderConfirmVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "预生产订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户对应的团长地址")
    private LeaderAddressVo leaderAddressVo;

    @ApiModelProperty(value = "购物项列表")
    private List<CartInfoVo> carInfoVoList;

    @ApiModelProperty(value = "订单优惠券列表")
    private List<CouponInfo> couponInfoList;

    @ApiModelProperty(value = "促销优惠金额")
    private BigDecimal activityReduceAmount;

    @ApiModelProperty(value = "优惠券优惠金额")
    private BigDecimal couponReduceAmount;

    @ApiModelProperty(value = "购物车原始总金额")
    private BigDecimal originalTotalAmount;

    @ApiModelProperty(value = "最终总金额")
    private BigDecimal totalAmount;

}
