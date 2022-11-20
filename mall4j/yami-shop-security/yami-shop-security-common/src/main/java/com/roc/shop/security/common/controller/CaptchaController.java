package com.roc.shop.security.common.controller;

import com.anji.captcha.model.common.RepCodeEnum;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description CaptchaController
 * @Author roc
 * @Date 2022/11/20 下午4:28
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/captcha")
@Api(tags = "验证码")
public class CaptchaController {

    private final CaptchaService captchaService;

    @PostMapping({ "/get" })
    public ResponseEntity<ResponseModel> get(@RequestBody CaptchaVO captchaVO) {
        return ResponseEntity.ok(captchaService.get(captchaVO));
    }
    @PostMapping({ "/check" })
    public ResponseEntity<ResponseModel> check(@RequestBody CaptchaVO captchaVO) {
        ResponseModel responseModel;
        try {
            responseModel = captchaService.check(captchaVO);
        }catch (Exception e) {
            return ResponseEntity.ok(ResponseModel.errorMsg(RepCodeEnum.API_CAPTCHA_COORDINATE_ERROR));
        }
        return ResponseEntity.ok(responseModel);
    }


}
