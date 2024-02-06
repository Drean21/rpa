package com.cy.rpa;

import com.alibaba.fastjson2.JSONObject;
import com.cy.assistant.AssistantWorker;
import com.cy.office.OfficeWorker;
import com.cy.os.OperatingSystemWorker;
import com.cy.web.WebWorker;

/**
 * JobWorker rpa任务基础父类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/02 18:40
 **/
public abstract class JobWorker<T> {


    // todo 组合方法将其他worker加进来达到多继承的类似效果
    // todo 任务会写和反馈方法封装
    public WebWorker web = new WebWorker();
    public OperatingSystemWorker os = new OperatingSystemWorker();
    public OfficeWorker office = new OfficeWorker();

    public AssistantWorker assistant =new AssistantWorker();

    /**
     * 有参json执行方法
     *
     * @param param
     */
    public abstract void worker(JSONObject param, String dataUrl) throws Exception;
}