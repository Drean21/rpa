package com.cy.rpa.exception;

import com.cy.RpaApplication;
import com.cy.rpa.feedback.FeedbackUtils;
import lombok.Data;


/**
 *  任务异常（所有非业务、超时之外的所有异常）
 */
@Data
public class JobException extends RuntimeException{
    protected Integer code;

    private String message;

    public JobException(String msg){
        super(msg);
        FeedbackUtils.highLevelException(msg);
        RpaApplication.shotdown();
    }
}
