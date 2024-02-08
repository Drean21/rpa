package com.cy.rpa.exception;

import com.cy.RpaApplication;
import com.cy.rpa.feedback.FeedbackUtils;

/**
 * 超时异常（元素未找到、结果未返回）
 */
public class TimeOutException extends RuntimeException {
    protected Integer code;

    private String message;

    public TimeOutException(String msg){
        super(msg);
        FeedbackUtils.highLevelException(msg);
        RpaApplication.shotdown();
    }
}
