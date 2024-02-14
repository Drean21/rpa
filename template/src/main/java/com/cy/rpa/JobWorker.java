package com.cy.rpa;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONObject;
import com.cy.RpaApplication;
import com.cy.rpa.behavior.web.Browser;
import com.cy.rpa.config.RPAConfig;
import com.cy.rpa.exception.BusinessException;
import com.cy.rpa.exception.TimeOutException;
import com.cy.rpa.feedback.FeedbackModel;
import com.cy.rpa.feedback.FeedbackUtils;
import com.cy.rpa.feedback.Result;
import com.cy.rpa.feedback.ResultStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * JobWorker rpa任务基础父类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/02 18:40
 **/
@Slf4j
public abstract class JobWorker<T> {

    // todo 任务会写和反馈方法封装
    public Browser browser = new Browser();

    //全局配置参数信息
    public JSONObject config;

    /**
     * 有参json执行方法
     *
     * @param param
     */
    public abstract void worker(JSONObject param, String dataUrl) throws Exception;


    /**
     * 增强worker方法
     *
     * @param param
     * @param dataUrl
     */
    public void handelWorker(JSONObject param, String dataUrl) {
        config=param;
        String envType = Objects.isNull(param) ?
                "dev" : param.getString("envType");
        log.info("环境类型:{}", envType);
        log.info("开始执行任务:{}", "dev".equals(envType) ? "" : param.toJSONString());
        try {
            worker(param, dataUrl);
        } catch (Exception e) {
            // todo 待完善处理
            if (e instanceof BusinessException) {
                log.error("业务异常:{}", e.getMessage());
            } else if (e instanceof TimeOutException) {
                log.info("超时异常:{}", e.getMessage());
            } else {
                log.info("程序异常:{}", e.getMessage());
            }
        } finally {
            //web.closeBrowser();
            if (!"dev".equalsIgnoreCase(envType)) {
                browser.closeBrowser();
            }
            // todo 检测cache文件夹，定期清理（找到最早的文件创建日期（修改日期）和当前时间比较，满足条件清理
        }
        //还要带上结束时间
        log.info("任务执行完成");
    }

    /**
     * Delays the execution of the current thread for the specified
     * time in milliseconds.
     *
     * @param time the length of time to sleep in milliseconds
     */
    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void log(String msg) {
        log.info(msg);
    }


    /**
     * 提示音
     */
    public static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    // todo 含压缩文件上传的回写方法重载！

    /**
     * 发送任务执行结果到RPA平台
     *
     * @param resultList 输出结果列表
     * @param workerName 应用名称
     * @param isSuccess  是否执行成功(1-成功，0-失败)
     * @param errorMsg   执行失败的错误提示
     */
    protected <B extends Result> void sendTaskExecuteResult(List<B> resultList, String workerName, Integer isSuccess, String errorMsg) {
        Class<B> cls = (Class<B>) resultList.get(0).getClass();
        try {
            // 写结果excel文件
            File file = new File(RPAConfig.logPath + File.separator + workerName + "_执行结果_" + DateUtil.now() + ".xlsx");
            EasyExcel.write(file, cls).sheet("结果").doWrite(resultList);
            // 计算成功的业务量
            int taskFinishNum = (int) resultList.stream().filter(item -> ResultStateEnum.WORK_SUCCESS.equals(item.getResult()) || ResultStateEnum.WORK_PENDING.equals(item.getResult())).count();
            // 组装反馈参数
            FeedbackModel model = new FeedbackModel();
            model.setTaskId(RpaApplication.taskId);
            model.setSuccess(isSuccess);
            int maxErrorMsgLength = 2000;
            if (StringUtils.isNotEmpty(errorMsg) && errorMsg.length() > maxErrorMsgLength) {
                errorMsg = errorMsg.substring(0, maxErrorMsgLength);
            }
            model.setMsg(errorMsg);
            model.setFile(file);
            if (RpaApplication.transactionNo == null) {
                log.error("交易号缺失，无法将结果反馈RPA平台，结果文件地址：{}", file.getAbsolutePath());
                return;
            }
            model.setTransactionNo(Long.valueOf(RpaApplication.transactionNo));
            model.setTaskFinishNum(taskFinishNum);
            model.setTotalNum(resultList.size());
            // 发送RPA平台执行结果
            String feedback = FeedbackUtils.feedback(model);
            log("反馈RPA平台响应：" + feedback);
            // 清理文件
            if (StringUtils.isNotBlank(feedback)) {
                JSONObject object = JSONObject.parseObject(feedback);
                Integer code = object.getInteger("code");
                if (code != null && HttpStatus.OK.value() == code) {
                    FileUtil.del(RPAConfig.downloadPath);
                    FileUtil.del(file);
                } else {
                    log("社保增减员结果反馈失败，结果文件地址：" + file.getAbsolutePath());
                }
            } else {
                log("社保增减员结果反馈失败，结果文件地址：" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            log.info("反馈RPA平台异常：{}", e);
        } finally {
            String localDevTest = System.getProperty("local.dev.test");
            if (!"true".equals(localDevTest)) {
                browser.closeBrowser();
            }
            beep();
        }
    }
    /**
     * 程序执行失败时，结果的导出与回写
     *
     * @param resultList 执行结果
     * @param errorMsg   异常中断原因
     */
    public <B extends Result> void sendFailResult(List<B> resultList, String errorMsg) {
        for (B item : resultList) {
            if (Objects.isNull(item.getResult())) {
                item.setResult(ResultStateEnum.WORK_FAIL);
                item.setReason(errorMsg);
            }
        }
        sendTaskExecuteResult(resultList, config.getString("应用名称"), 0, errorMsg);
    }

    /**
     * 执行成功情况下的结果导出与回写
     *
     * @param resultList 执行结果
     */
    public <B extends Result> void sendSuccessResult(List<B> resultList) {
        sendTaskExecuteResult(resultList, config.getString("应用名称"), 1, "成功");
    }
}