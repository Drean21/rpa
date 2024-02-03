package com.cy.rpa.exception;

/**
 * 自定义业务异常(DateUtils工具类使用到的)
 *
 * @author junrunrenli
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected Integer code;

    private String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

}
