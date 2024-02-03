package com.cy.rpa.exception;

import com.cy.RpaApplication;
import com.cy.rpa.feedback.FeedbackUtils;

/**
 * @author wjk
 * @Title:
 * @Package com.jrrl.rpa.exception
 * @Description:
 * @date 2022/6/2 13:37
 */
public class JobException extends RuntimeException{
    public JobException(String msg){
        super(msg);
        FeedbackUtils.highLevelException(msg);
        RpaApplication.shotdown();
    }
}
