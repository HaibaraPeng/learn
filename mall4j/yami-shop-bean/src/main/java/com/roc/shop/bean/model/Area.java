package com.roc.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description Area
 * @Author roc
 * @Date 2022/11/26 下午3:26
 */
@Data
@TableName("tz_area")
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    @ApiModelProperty(value = "地区id",required=true)
    private Long areaId;

    @ApiModelProperty(value = "地区名称",required=true)
    private String areaName;

    @ApiModelProperty(value = "地区上级id",required=true)
    private Long parentId;

    @ApiModelProperty(value = "地区层级",required=true)
    private Integer level;

    @TableField(exist=false)
    private List<Area> areas;
}
