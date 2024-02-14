package com.cy.rpa.feedback;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.cy.rpa.config.ResultStateEnumConverter;
import lombok.Data;

import java.io.File;

/**
 * Result 任务执行结果基础字段类
 * 用于封装任务执行结果，并导出为Excel文件
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/13 22:25
 **/
@Data
@HeadRowHeight(26)
@HeadStyle(fillForegroundColor = 71)
public class Result {
    /** 数据唯一标识 (订单交易号)*/
    @ExcelProperty("数据唯一标识")
    @ColumnWidth(50)
    public String dataId;

    /** 执行结果（未执行、成功、失败、待审核）, 从ResultStateEnum中获取 */
    @ExcelProperty(value = "执行结果", converter = ResultStateEnumConverter.class)
    @ColumnWidth(20)
    public ResultStateEnum result;

    /** 详情，对执行结果的描述 */
    @ExcelProperty("详情")
    @ColumnWidth(40)
    public String reason;

    @ExcelProperty("详情图片")
    @ColumnWidth(50)
    public File image;
}