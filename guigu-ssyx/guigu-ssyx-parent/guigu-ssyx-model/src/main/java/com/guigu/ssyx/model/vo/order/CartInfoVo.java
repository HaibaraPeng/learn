package com.guigu.ssyx.model.vo.order;

import com.guigu.ssyx.model.entity.activity.ActivityRule;
import com.guigu.ssyx.model.entity.order.CartInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Roc
 * @Date 2025/1/4 17:36
 */
@Data
public class CartInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物项凑单，同一活动对应的最优活动规则
     */
    @ApiModelProperty(value = "cartInfoList")
    private List<CartInfo> cartInfoList;

    @ApiModelProperty(value = "活动规则")
    private ActivityRule activityRule;


}
