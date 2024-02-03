package com.cy.rpa.annotations;

import java.lang.annotation.*;

/**
 * rpa工作任务标住注解
 * String appName，标住rpa工作任务名称
 * String appCode，标住rpa工作任务appCode标识
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Jobs.class)
public @interface Job {
    String appName();
    String appCode();
}
