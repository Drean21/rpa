package com.cy.web.listeners;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

/**
 * CustomEventListener 自定义网页元素监听器
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/07 21:58
 **/
@Slf4j
public class CustomEventListener extends AbstractWebDriverEventListener {
    /**
     * 模拟正常浏览器访问户行为，隐藏 navigator.webdriver 属性
     * @param url
     * @param driver
     */
    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
        try {
            log.info("afterNavigateTo: {}", url);
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        } catch (Exception e) {
            log.warn("隐藏 navigator.webdriver 属性 出现异常: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}