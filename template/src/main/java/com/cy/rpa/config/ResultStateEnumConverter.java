package com.cy.rpa.config;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.StringUtils;
import com.cy.rpa.feedback.ResultStateEnum;

/**
 *执行结果枚举转换类
 */
public class ResultStateEnumConverter implements Converter<ResultStateEnum> {

    @Override
    public Class<ResultStateEnum> supportJavaTypeKey() {
        return ResultStateEnum.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    // EasyExcel 3.x版本的convertToJavaData方法实现
    @Override
    public ResultStateEnum convertToJavaData(ReadConverterContext<?> context) {
        CellData cellData = context.getReadCellData();
        if (cellData == null || StringUtils.isEmpty(cellData.getStringValue())) {
            return null;
        }
        return ResultStateEnum.getResultStateEnum(cellData.getStringValue());
    }

    // EasyExcel 3.x版本的convertToExcelData方法实现
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<ResultStateEnum> context) {
        ResultStateEnum value = context.getValue();
        if (value == null) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(value.getState());
    }
}
