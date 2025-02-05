package com.ruoyi.cloud.api.system.factory;

import com.ruoyi.cloud.api.system.RemoteFileService;
import com.ruoyi.cloud.api.system.domain.SysFile;
import com.ruoyi.cloud.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Roc
 * @Date 2025/01/17 22:48
 */
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteFileFallbackFactory.class);

    @Override
    public RemoteFileService create(Throwable throwable) {
        log.error("文件服务调用失败:{}", throwable.getMessage());
        return new RemoteFileService() {
            @Override
            public R<SysFile> upload(MultipartFile file) {
                return R.fail("上传文件失败:" + throwable.getMessage());
            }
        };
    }
}
