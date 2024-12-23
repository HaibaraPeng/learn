package com.guigu.ssyx.service.util.exception;

import com.guigu.ssyx.service.util.result.ResultCodeEnum;
import lombok.Data;

/**
 * @Author Roc
 * @Date 2024/12/23 17:10
 */
@Data
public class SsyxException extends RuntimeException {

    //异常状态码
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     *
     * @param message
     * @param code
     */
    public SsyxException(String message, Integer code) {
        super(message);
        this.code = code;
    }


    /**
     * 接收枚举类型对象
     *
     * @param resultCodeEnum
     */
    public SsyxException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

}
