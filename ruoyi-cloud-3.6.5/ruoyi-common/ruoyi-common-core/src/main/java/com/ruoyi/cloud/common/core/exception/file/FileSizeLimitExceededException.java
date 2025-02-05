package com.ruoyi.cloud.common.core.exception.file;

/**
 * @Author Roc
 * @Date 2025/02/04 15:58
 */
public class FileSizeLimitExceededException extends FileException {
    private static final long serialVersionUID = 1L;

    public FileSizeLimitExceededException(long defaultMaxSize) {
        super("upload.exceed.maxSize", new Object[]{defaultMaxSize}, "the filesize is too large");
    }
}
