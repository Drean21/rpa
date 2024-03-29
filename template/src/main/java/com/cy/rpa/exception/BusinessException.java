package com.cy.rpa.exception;

import com.cy.RpaApplication;
import com.cy.rpa.feedback.FeedbackUtils;
import lombok.Data;

/**
 * 业务异常（业务操作错误导致的异常，如映射错误、操作步骤错误（网页元素变更等））
 */
@Data
public class BusinessException extends RuntimeException {
    protected Integer code;

    private String message;

    public BusinessException(String msg){
        super(msg);
        FeedbackUtils.highLevelException(msg);
        RpaApplication.shotdown();
    }
}
