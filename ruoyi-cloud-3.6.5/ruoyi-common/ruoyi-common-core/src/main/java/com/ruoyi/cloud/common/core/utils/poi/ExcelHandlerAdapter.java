package com.ruoyi.cloud.common.core.utils.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @Author Roc
 * @Date 2025/01/08 23:09
 */
public interface ExcelHandlerAdapter {
    /**
     * 格式化
     *
     * @param value 单元格数据值
     * @param args  excel注解args参数组
     * @param cell  单元格对象
     * @param wb    工作簿对象
     * @return 处理后的值
     */
    Object format(Object value, String[] args, Cell cell, Workbook wb);
}
