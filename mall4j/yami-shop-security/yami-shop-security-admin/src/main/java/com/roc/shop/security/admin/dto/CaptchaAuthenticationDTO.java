package com.roc.shop.security.admin.dto;

import com.roc.shop.security.common.dto.AuthenticationDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 验证码登录
 * @Author roc
 * @Date 2022/11/21 下午10:19
 */
@Data
public class CaptchaAuthenticationDTO extends AuthenticationDTO {

    @ApiModelProperty(value = "验证码", required = true)
    private String captchaVerification;
}
