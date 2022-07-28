package com.roc.utils;

import lombok.experimental.UtilityClass;

/**
 * @Description StringUtil
 * @Author penn
 * @Date 2022/7/28 22:54
 */
@UtilityClass
public class StringUtil {
    public boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
