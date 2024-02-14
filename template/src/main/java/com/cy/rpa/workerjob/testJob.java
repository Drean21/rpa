package com.cy.rpa.workerjob;

import com.alibaba.fastjson2.JSONObject;
import com.cy.rpa.JobWorker;
import com.cy.rpa.behavior.Robot;
import com.cy.rpa.toolkit.OcrUtil;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.openqa.selenium.By;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

import java.io.IOException;

/**
 * testJob rpa任务测试Job
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/03 10:21
 **/
public class testJob extends JobWorker<T> {
    @Test
    public void test() {
        handelWorker(null, null);
    }


    @Override
    public void worker(JSONObject param, String dataUrl) throws Exception {


    }


    @Test
    public void testDowload() {
        browser.initChrome();
        browser.openUrl("https://www.zhihu.com/");
        browser.clickElement(By.xpath("//button[text()='下载知乎App']"));
        browser.clickElement(By.xpath("//*[@id=\"root\"]/div[4]/div/div[1]/div[2]/div[1]/div[3]/a[2]"));
        Robot.specifyPathAndSaveFile("D:\\app");
    }

    @Test
    public void testClickByImg() throws IOException, FindFailed {
        browser.initChrome();
        browser.openUrl("https://translate.google.com/?hl=zh-CN&sl=auto&tl=en&op=translate");
        //Robot.clickByImg("D:/login.png");
        org.sikuli.basics.Settings.setImageCache(0);
        Screen screen = new Screen();
        screen.click("D:/login.png");
        //screen.click();
    }
    @Test
    public void testOcr() throws IOException {
        browser.initChrome();
        browser.openUrl("https://translate.google.com/?hl=zh-CN&sl=auto&tl=en&op=translate");
        browser.setInputValue(By.xpath("//*[@id=\"yDmH0d\"]/c-wiz/div/div[2]/c-wiz/div[2]/c-wiz/div[1]/div[2]/div[2]/c-wiz[1]/span/span/div/textarea"),"你好,我的朋友");
        String s = browser.captureElementScreenshot(By.xpath("//*[@id=\"yDmH0d\"]/c-wiz/div/div[2]/c-wiz/div[2]/c-wiz/div[1]/div[2]/div[2]/c-wiz[1]/span"));
        String text = OcrUtil.ocrByTesseract(s);
        System.out.println(text);
    }

}