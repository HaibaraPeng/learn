package com.ruoyi.cloud.modules.system.controller;

import com.ruoyi.cloud.api.system.domain.SysOperLog;
import com.ruoyi.cloud.common.core.web.controller.BaseController;
import com.ruoyi.cloud.common.core.web.domain.AjaxResult;
import com.ruoyi.cloud.common.security.annotation.InnerAuth;
import com.ruoyi.cloud.modules.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author Roc
 * @Date 2025/01/17 22:18
 */
@RestController
@RequestMapping("/operlog")
public class SysOperlogController extends BaseController {
    @Autowired
    private ISysOperLogService operLogService;

//    @RequiresPermissions("system:operlog:list")
//    @GetMapping("/list")
//    public TableDataInfo list(SysOperLog operLog) {
//        startPage();
//        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
//        return getDataTable(list);
//    }
//
//    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
//    @RequiresPermissions("system:operlog:export")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SysOperLog operLog) {
//        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
//        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
//        util.exportExcel(response, list, "操作日志");
//    }
//
//    @Log(title = "操作日志", businessType = BusinessType.DELETE)
//    @RequiresPermissions("system:operlog:remove")
//    @DeleteMapping("/{operIds}")
//    public AjaxResult remove(@PathVariable Long[] operIds) {
//        return toAjax(operLogService.deleteOperLogByIds(operIds));
//    }
//
//    @RequiresPermissions("system:operlog:remove")
//    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
//    @DeleteMapping("/clean")
//    public AjaxResult clean() {
//        operLogService.cleanOperLog();
//        return success();
//    }

    @InnerAuth
    @PostMapping
    public AjaxResult add(@RequestBody SysOperLog operLog) {
        return toAjax(operLogService.insertOperlog(operLog));
    }
}
