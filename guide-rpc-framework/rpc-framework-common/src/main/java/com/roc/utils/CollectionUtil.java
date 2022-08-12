package com.roc.utils;

import lombok.experimental.UtilityClass;

import java.util.Collection;

/**
 * @Description CollectionUtil
 * @Author dongp
 * @Date 2022/8/11 0011 17:23
 */
@UtilityClass
public class CollectionUtil {

    public boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
