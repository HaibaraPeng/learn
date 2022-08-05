package com.roc.utils;

import lombok.experimental.UtilityClass;

/**
 * @Description RuntimeUtil
 * @Author dongp
 * @Date 2022/8/2 0002 11:03
 */
@UtilityClass
public final class RuntimeUtil {

    /**
     * 获取CPU的核心数
     *
     * @return
     */
    public int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
