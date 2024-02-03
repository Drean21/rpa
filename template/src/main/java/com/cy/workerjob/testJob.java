package com.cy.workerjob;

import com.alibaba.fastjson2.JSONObject;
import com.cy.rpa.JobWorker;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;

/**
 * testJob rpa任务测试Job
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/03 10:21
 **/
public class testJob extends JobWorker<T> {
    @Test
    public void test(){
        worker(null,null);
    }

    @Override
    public void worker(JSONObject param, String dataUrl) {
        // todo 不返回webDrive，而是自己一套webDriver包含其中？还是都开放
        web.initChrome();
        web.openUrl("https://www.hutool.cn/docs/#/core/Codec%E7%BC%96%E7%A0%81/Base64%E7%BC%96%E7%A0%81%E8%A7%A3%E7%A0%81-Base64?id=base64%e7%bc%96%e7%a0%81%e8%a7%a3%e7%a0%81-base64");

    }
}