package com.cy.rpa;

import com.alibaba.fastjson2.JSONObject;
import com.cy.assistant.AssistantWorker;
import com.cy.office.OfficeWorker;
import com.cy.os.OperatingSystemWorker;
import com.cy.rpa.exception.BusinessException;
import com.cy.rpa.exception.TimeOutException;
import com.cy.web.WebWorker;
import lombok.extern.slf4j.Slf4j;

/**
 * JobWorker rpa任务基础父类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/02 18:40
 **/
@Slf4j
public abstract class JobWorker<T> {


    // todo 组合方法将其他worker加进来达到多继承的类似效果
    // todo 任务会写和反馈方法封装
    public WebWorker web = new WebWorker();
    public OperatingSystemWorker os = new OperatingSystemWorker();
    public OfficeWorker office = new OfficeWorker();

    public AssistantWorker assistant = new AssistantWorker();

    /**
     * 有参json执行方法
     *
     * @param param
     */
    public abstract void worker(JSONObject param, String dataUrl) throws Exception;


    /**
     * 增强worker方法
     * @param param
     * @param dataUrl
     */
    public void handelWorker(JSONObject param, String dataUrl) {
        log.info("开始执行任务:{}", param.toJSONString());
        try {
            worker(param, dataUrl);
        } catch (Exception e) {
            // todo 待完善处理
            if (e instanceof BusinessException){
                log.error("业务异常:{}",e.getMessage());
            }else if (e instanceof TimeOutException){
                log.info("超时异常:{}",e.getMessage());
            }else{
                log.info("程序异常:{}",e.getMessage());
            }
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
}