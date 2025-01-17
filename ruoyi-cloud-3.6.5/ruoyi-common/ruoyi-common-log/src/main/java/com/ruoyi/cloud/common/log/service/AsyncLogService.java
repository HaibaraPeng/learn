package com.ruoyi.cloud.common.log.service;

import com.ruoyi.cloud.api.system.RemoteLogService;
import com.ruoyi.cloud.api.system.domain.SysOperLog;
import com.ruoyi.cloud.common.core.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author Roc
 * @Date 2025/01/17 22:00
 */
@Service
public class AsyncLogService {
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog) throws Exception {
        remoteLogService.saveLog(sysOperLog, SecurityConstants.INNER);
    }
}
