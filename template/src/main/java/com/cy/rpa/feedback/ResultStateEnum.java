package com.cy.rpa.feedback;

import java.util.Arrays;
import java.util.Optional;

/**
 * 数据执行结果枚举
 */
public enum ResultStateEnum {
    /** 未执行 */
    WORK_UNEXECUTED("未执行"),

    /** 成功 */
    WORK_SUCCESS("成功"),

    /** 失败 */
    WORK_FAIL("失败"),

    /** 待审核 */
    WORK_PENDING("待审核");

    /**
     * 执行状态
     */
    private String state;

    ResultStateEnum(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String msg) {
        this.state = msg;
    }

    /**
     * @param stateString 状态
     */
    public static ResultStateEnum getResultStateEnum(String stateString){
        Optional<ResultStateEnum> first = Arrays.stream(ResultStateEnum.values()).filter(item -> item.state.equals(stateString)).findFirst();
        return first.orElse(null);
    }
}
