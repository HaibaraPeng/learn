package com.ruoyi.cloud.common.core.exception.file;

import com.ruoyi.cloud.common.core.exception.base.BaseException;

/**
 * @Author Roc
 * @Date 2025/02/04 15:58
 */
public class FileException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args, String msg) {
        super("file", code, args, msg);
    }

}
