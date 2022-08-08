package com.roc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description CompressTypeEnum
 * @Author dongp
 * @Date 2022/8/8 0008 15:32
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
