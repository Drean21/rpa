package com.cy.rpa.behavior.office;

import cn.hutool.http.HttpUtil;
import com.alibaba.excel.EasyExcel;
import com.cy.rpa.config.EasyExcelListener;
import com.cy.rpa.config.RPAConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ExcelUtil Excel工具类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/13 21:56
 **/
@Slf4j
public class ExcelUtil {
    /**
     * 从excel文件中读取数据的方法，使用默认的sheet页码和起始行号
     * @param fileName 文件名或网络路径，如果路径以http开始，则会下载此文件
     * @param cls 用作解析excel的Java类，这个类的字段会与excel的列进行映射
     * @return 返回解析后的数据列表，若读取失败或者解析异常，返回空列表
     */
    public static List<T> readExcel(String fileName,Class cls) {
        return readExcel(fileName,cls,0,1);
    }
    /**
     * 以指定的类作为模板，从excel文件中读取指定sheet中的指定行数据的方法
     * @param fileName 文件名或网络路径，如果路径以http开始，则会下载此文件
     * @param cls 用作解析excel的Java类，这个类的字段会与excel的列进行映射
     * @param sheetNo 需要读取数据的sheet页码
     * @param headRowNum 需要读取数据的起始行号
     * @return 返回解析后的数据列表，若读取失败或者解析异常，返回空列表
     */
    public static List<T> readExcel(String fileName, Class cls, int sheetNo,int headRowNum) {
        List<T> list = null;
        try {
            if(fileName.startsWith("http")){
                String localPath= RPAConfig.cachePath+File.separator+System.currentTimeMillis()+".xlsx";
                long size = HttpUtil.downloadFile(fileName, localPath);
                fileName=localPath;
                log.info("下载文件成功，文件大小：{}", size);
            }
            EasyExcelListener<T> easyExcelListener = new EasyExcelListener<>();
            EasyExcel.read(fileName, cls,easyExcelListener).sheet(sheetNo).headRowNumber(headRowNum).doRead();
            list = easyExcelListener.getDataList();
        } catch (Exception e) {
            log.error("员工数据表数据解析异常：" + e.getMessage() + "\nsheetName:" + sheetNo);
            list = new ArrayList<>();
        }
        if (list.size() == 0) {
            log.error("数据表解析失败:未获取到数据;请检查配置文件;");
        }
        return list;
    }

    /**
     * 从excel文件中读取数据的方法，默认读取第一个sheet的第一行数据
     * @param fileName 文件名或网络路径，如果路径以http开始，则会下载此文件
     * @return 返回解析后的数据列表，如果读取失败或者解析异常，返回空列表
     */
    public static List<T> readExcel(String fileName) {
        return readExcel(fileName, 0, 1);
    }
    /**
     * 从excel文件中读取指定行数据的方法
     * @param fileName 文件名或网络路径，如果路径以http开始，则会下载此文件
     * @param sheetNo 需要读取数据的sheet页码
     * @param headRowNum 需要读取数据的起始行号
     * @return 返回解析后的数据列表，若读取失败或者解析异常，返回空列表
     */
    public static List<T> readExcel(String fileName, int sheetNo,int headRowNum) {
        List<T> list = null;
        try {
            if(fileName.startsWith("http")){
                String localPath= RPAConfig.cachePath+File.separator+System.currentTimeMillis()+".xlsx";
                long size = HttpUtil.downloadFile(fileName, localPath);
                fileName=localPath;
                log.info("下载文件成功，文件大小：{}", size);
            }
            EasyExcelListener<T> easyExcelListener = new EasyExcelListener<>();
            EasyExcel.read(fileName, easyExcelListener).sheet(sheetNo).headRowNumber(headRowNum).doRead();
            list = easyExcelListener.getDataList();
        } catch (Exception e) {
            log.error("员工数据表数据解析异常：" + e.getMessage() + "\nsheetName:" + sheetNo);
            list = new ArrayList<>();
        }
        if (list.size() == 0) {
            log.error("数据表解析失败:未获取到数据;请检查配置文件;");
        }
        return list;
    }

    /**
     * 从excel文件中读取指定sheet中的指定行数据的方法
     * @param fileName 文件名或网络路径，如果路径以http开始，则会下载此文件
     * @param sheetName 需要读取数据的sheet页名称
     * @param headRowNum 需要读取数据的起始行号
     * @return 返回解析后的数据列表，若读取失败或者解析异常，返回空列表
     */
    public static List<T> readExcel(String fileName, String sheetName,int headRowNum) {
        List<T> list = null;
        try {
            if(fileName.startsWith("http")){
                String localPath= RPAConfig.cachePath+File.separator+System.currentTimeMillis()+".xlsx";
                long size = HttpUtil.downloadFile(fileName, localPath);
                fileName=localPath;
                log.info("下载文件成功，文件大小：{}", size);
            }
            EasyExcelListener<T> easyExcelListener = new EasyExcelListener<>();
            EasyExcel.read(fileName, easyExcelListener).sheet(sheetName).headRowNumber(headRowNum).doRead();
            list = easyExcelListener.getDataList();
        } catch (Exception e) {
            log.error("员工数据表数据解析异常：" + e.getMessage() + "\nsheetName:" + sheetName);
            list = new ArrayList<>();
        }
        if (list.size() == 0) {
            log.error("数据表解析失败:未获取到数据;请检查配置文件;");
        }
        return list;
    }
}