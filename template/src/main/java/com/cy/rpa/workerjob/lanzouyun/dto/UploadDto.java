package com.cy.rpa.workerjob.lanzouyun.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cy.rpa.feedback.Result;
import lombok.Data;

/**
 * UploadDto 上传任务Excel属性列
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/13 22:19
 **/
@Data
public class UploadDto extends Result {
    @ExcelProperty("待上传文件")
    private String fileName;

    @ExcelProperty("文件路径")
    private String filePath;

    @ExcelProperty("任务下发时间")
    private String taskTime;
}