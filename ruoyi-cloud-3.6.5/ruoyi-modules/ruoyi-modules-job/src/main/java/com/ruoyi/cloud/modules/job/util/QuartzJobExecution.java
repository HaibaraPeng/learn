package com.ruoyi.cloud.modules.job.util;

import com.ruoyi.cloud.modules.job.domain.SysJob;
import org.quartz.JobExecutionContext;

/**
 * @Author Roc
 * @Date 2025/02/16 11:03
 */
public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
