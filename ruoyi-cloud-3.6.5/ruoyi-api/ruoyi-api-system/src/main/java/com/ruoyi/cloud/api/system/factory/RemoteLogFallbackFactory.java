package com.ruoyi.cloud.api.system.factory;

import com.ruoyi.cloud.api.system.RemoteLogService;
import com.ruoyi.cloud.api.system.domain.SysLogininfor;
import com.ruoyi.cloud.api.system.domain.SysOperLog;
import com.ruoyi.cloud.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author Roc
 * @Date 2025/01/10 21:35
 */
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteLogFallbackFactory.class);

    @Override
    public RemoteLogService create(Throwable throwable) {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLogService() {
            @Override
            public R<Boolean> saveLog(SysOperLog sysOperLog, String source) {
                return R.fail("保存操作日志失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> saveLogininfor(SysLogininfor sysLogininfor, String source) {
                return R.fail("保存登录日志失败:" + throwable.getMessage());
            }
        };

    }
}
