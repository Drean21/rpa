package com.cy.rpa.feedback;

import lombok.Data;

import java.io.File;

/**
 * @author wjk
 * @Title: 任务反馈参数模型
 * @Package com.jrrl.rpa.feedback
 * @Description:
 * @date 2022/5/18 16:14
 */
@Data
public class FeedbackModel {
    private String taskId; //任务id
    private String commitId; //任务id

    //private String address; //文件地址

    private Integer success; //0失败，1成功
    //任务成功数量
    private Integer taskFinishNum;

    // 任务总数
    private Integer totalNum;

    //执行产物文件
    private File file;

    //交易号
    private Long transactionNo;

    private String msg; //执行结果消息

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.commitId = taskId;
        this.taskId = taskId;
    }
}
