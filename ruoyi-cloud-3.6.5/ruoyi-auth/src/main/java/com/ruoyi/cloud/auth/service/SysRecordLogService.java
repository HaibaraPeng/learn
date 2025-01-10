package com.ruoyi.cloud.auth.service;

import com.ruoyi.cloud.api.system.RemoteLogService;
import com.ruoyi.cloud.api.system.domain.SysLogininfor;
import com.ruoyi.cloud.common.core.constant.Constants;
import com.ruoyi.cloud.common.core.constant.SecurityConstants;
import com.ruoyi.cloud.common.core.utils.StringUtils;
import com.ruoyi.cloud.common.core.utils.ip.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Roc
 * @Date 2025/01/08 23:20
 */
@Component
public class SysRecordLogService {
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String username, String status, String message) {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(IpUtils.getIpAddr());
        logininfor.setMsg(message);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus(Constants.LOGIN_SUCCESS_STATUS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            logininfor.setStatus(Constants.LOGIN_FAIL_STATUS);
        }
        remoteLogService.saveLogininfor(logininfor, SecurityConstants.INNER);
    }
}
