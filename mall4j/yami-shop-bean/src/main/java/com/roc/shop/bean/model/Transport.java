package com.roc.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description Transport
 * @Author roc
 * @Date 2022/11/26 下午3:24
 */
@Data
@TableName("tz_transport")
public class Transport implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 运费模板id
     */
    @TableId
    @ApiModelProperty(value = "运费模板id", required = true)
    private Long transportId;

    /**
     * 运费模板名称
     */
    @ApiModelProperty(value = "运费模板名称", required = true)
    private String transName;

    /**
     * 创建时间
     */

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

    /**
     * 店铺id
     */

    @ApiModelProperty(value = "店铺id", required = true)
    private Long shopId;

    /**
     * 参考 TransportChargeType
     * 收费方式（0 按件数,1 按重量 2 按体积）
     */
    @ApiModelProperty(value = "收费方式（0 按件数,1 按重量 2 按体积）", required = true)
    private Integer chargeType;


    /**
     * 是否包邮 0:不包邮 1:包邮
     */
    @ApiModelProperty(value = "是否包邮 0:不包邮 1:包邮", required = true)
    private Integer isFreeFee;

    /**
     * 是否含有包邮条件
     */
    @ApiModelProperty(value = "是否含有包邮条件", required = true)
    private Integer hasFreeCondition;

    /**
     * 指定条件包邮项
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "指定条件包邮项", required = true)
    private List<TransfeeFree> transfeeFrees;

    /**
     * 运费项
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "运费项", required = true)
    private List<Transfee> transfees;
}
