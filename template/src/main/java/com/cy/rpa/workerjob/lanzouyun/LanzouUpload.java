package com.cy.rpa.workerjob.lanzouyun;

import com.alibaba.fastjson2.JSONObject;
import com.cy.rpa.JobWorker;
import com.cy.rpa.annotations.Job;
import com.cy.rpa.behavior.Robot;
import com.cy.rpa.behavior.office.ExcelUtil;
import com.cy.rpa.feedback.ResultStateEnum;
import com.cy.rpa.workerjob.lanzouyun.dto.UploadDto;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * LanzouUpload 蓝奏云上传demo
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/12 12:54
 **/
@Job(appName = "蓝奏云上传demo", appCode = "lanZouYun_Upload")
public class LanzouUpload extends JobWorker {
    @Test
    public void testJob() {
        JSONObject config = new JSONObject();
        config.put("账号", "18900802293");
        config.put("密码", "********");
        handelWorker(config, "D:\\Desktop\\demo\\蓝奏云上传任务.xlsx");
    }

    @Override
    public void worker(JSONObject param, String dataUrl) throws Exception {
        // 打开网页
        browser.openUrl("https://up.woozooo.com/");
        By loginBtn = By.xpath("//a[text()='登 录']");
        WebElement element = browser.getElement(loginBtn, 2000);
        if (!Objects.isNull(element)) {
            log("未登录");
            // 点击元素
            browser.clickElement(loginBtn);
            browser.switchTabByTitle("网盘用户登录");
            // 机器人-模拟粘贴
            //Robot.setInputByPaste("18900802293");
            // 设置输入框
            browser.setInputValue(By.id("username"), config.getString("账号"));
            browser.setInputValue(By.name("password"), config.getString("密码"));
            // 简单滑块
            browser.dragSlider(By.id("nc_1_n1z"), By.id("nc_1__scale_text"));
            // 登录
            browser.clickElement(By.id("s3"));
        }
        log("登录成功");
        browser.clickElement(By.xpath("//a[text()='控制台']"));
        browser.switchTabByUrl("https://up.woozooo.com/u");

        List<UploadDto> uploadDtoList = ExcelUtil.readExcel(dataUrl, UploadDto.class);
        uploadDtoList.forEach(t -> {
            log("正在上传文件：" + t.getFileName());
            switchUploadFrame();
            // 点击上传
            //browser.clickElement(By.xpath("//a[text()='上传文件']"));
            browser.clickElement(By.cssSelector(".diskdao3"));
            switchUploadFrame();
            // 点击上传文件
            String text = browser.getElement(By.cssSelector("div#filePicker > .webuploader-pick")).getText();
            log("结果：" + text);
            browser.clickElement(By.cssSelector("div#filePicker > .webuploader-pick"));
            Robot.uploadFileByPath(t.getFilePath());
            switchUploadFrame();
            // 点击开始上传
            browser.clickElement(By.cssSelector(".state-ready.uploadBtn"));
            // 等待查找被隐藏的上传弹窗元素
            switchUploadFrame();
            WebElement hideUpload = browser.getElement(By.xpath("//div[@id='container']//div[@class='f_upb' and @style='top: -1000px;']"), 3 * 60 * 1000);
            // 上传的弹窗被隐藏，证明上传成功
            if (hideUpload != null) {
                log("上传成功");
                t.setResult(ResultStateEnum.WORK_SUCCESS);
            } else {
                log("上传失败");
                t.setResult(ResultStateEnum.WORK_FAIL);
                // 失败屏幕截图
                String s = browser.captureFullPageScreenshot();
                t.setImage(new File(s));
            }
        });
        // 任务结果回写
        sendSuccessResult(uploadDtoList);
    }


    /**
     * 切换到上传页面iframe
     */
    private void switchUploadFrame() {
        // 建议：重新回到顶层操作元素（不易出错）
        browser.switchToDefaultContent();
        //// 进入一级iframe
        browser.switchToFrame(By.xpath("/html//iframe[@id='mainframe']"));
        //// 进入二级iframe
        browser.switchToFrame(By.xpath("/html//iframe[@id='mainframe']"));
    }
}