package com.guigu.ssyx.service.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Roc
 * @Date 2024/12/25 15:15
 */
public interface FileUploadService {

    //图片上传的方法
    String uploadFile(MultipartFile file);
}
