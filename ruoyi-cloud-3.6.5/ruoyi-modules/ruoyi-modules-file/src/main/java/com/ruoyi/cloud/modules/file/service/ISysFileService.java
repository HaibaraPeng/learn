package com.ruoyi.cloud.modules.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Roc
 * @Date 2025/01/19 22:24
 */
public interface ISysFileService {
    /**
     * 文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception;
}
