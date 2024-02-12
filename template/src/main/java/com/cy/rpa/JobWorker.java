package com.cy.rpa;

import com.alibaba.fastjson2.JSONObject;
import com.cy.rpa.behavior.web.Browser;
import com.cy.rpa.exception.BusinessException;
import com.cy.rpa.exception.TimeOutException;
import lombok.extern.slf4j.Slf4j;

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
}