package com.guigu.ssyx.model.entity.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.guigu.ssyx.model.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2024/12/25 14:47
 */
@Data
@ApiModel(description = "CommentReplay")
@TableName("comment_replay")
public class CommentReplay extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "commentId")
    @TableField("comment_id")
    private Long commentId;

    @ApiModelProperty(value = "nickName")
    @TableField("nick_name")
    private String nickName;

    @ApiModelProperty(value = "icon")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "content")
    @TableField("content")
    private String content;

}
