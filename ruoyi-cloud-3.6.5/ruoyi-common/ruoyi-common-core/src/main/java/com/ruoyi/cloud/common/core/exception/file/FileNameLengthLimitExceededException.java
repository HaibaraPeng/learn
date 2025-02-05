package com.ruoyi.cloud.common.core.exception.file;

/**
 * @Author Roc
 * @Date 2025/02/04 16:00
 */
public class FileNameLengthLimitExceededException extends FileException {
    private static final long serialVersionUID = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super("upload.filename.exceed.length", new Object[]{defaultFileNameLength}, "the filename is too long");
    }
}
