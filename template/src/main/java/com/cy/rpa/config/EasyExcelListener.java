package com.cy.rpa.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EasyExcelListener EasyExcel监听器
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/13 22:10
 **/
public class EasyExcelListener <T> extends AnalysisEventListener<T> {
    // Excel数据
    private List<T> dataList = new ArrayList<>();
    // 错误信息
    private List<String> errorMessages = new ArrayList<>();

    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据解析完成！");
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        // 记录错误信息
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelException = (ExcelDataConvertException) exception;
            String errorMessage = String.format("第%s行，第%s列解析错误，错误原因：%s",
                    excelException.getRowIndex() + 1,
                    excelException.getColumnIndex() + 1,
                    exception.getMessage());
            errorMessages.add(errorMessage);
        } else {
            errorMessages.add("解析异常：" + exception.getMessage());
        }
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头信息：" + headMap);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}