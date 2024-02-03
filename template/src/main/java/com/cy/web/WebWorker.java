package com.cy.web;

import com.cy.rpa.RPAConfig;
import lombok.Data;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.File;

/**
 * WebWorker 网页自动化根类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/01 19:07
 **/
@Data
public class WebWorker {

    /*浏览器驱动*/
    private WebDriver driver;
    /*js执行器*/
    private JavascriptExecutor js;

    /*Action类 链式网页操作*/
    private Actions action;


    public void initChrome() {
        try {
            // 设置 ChromeDriver 路径
            String chromeDriverPath = RPAConfig.envPath + File.separator + "drivers/chromedriver.exe";
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            // 设置 Chrome 可执行文件路径
            String chromePath = RPAConfig.envPath + File.separator + "chrome-win64/chrome.exe";
            ChromeOptions options = new ChromeOptions();
            options.setBinary(chromePath);

            // 添加其他 ChromeOptions 设置（可根据需要自行添加）
             options.addArguments("--start-maximized"); // 最大化窗口
            // options.addArguments("--headless"); // 无头模式
            options.addArguments("--remote-allow-origins=*");//解决 403 出错问题

            // 创建 ChromeDriver 实例
            driver = new ChromeDriver(options);

            // 可选：设置隐式等待时间
            // driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // 可选：最大化窗口
            // driver.manage().window().maximize();
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            // 可添加其他处理逻辑，如日志记录、报警等
        }
    }

    // 可以在测试结束时调用该方法来关闭浏览器
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    public void initEdge() {
        String edgeDrivePath = RPAConfig.envPath+ File.separator+ "drivers/msedgedriver.exe";
        String edgePath = RPAConfig.envPath+ File.separator+"chrome-win64/chrome.exe";
        System.setProperty("webdriver.chrome.driver", edgeDrivePath);
        EdgeOptions options = new EdgeOptions();
        options.setBinary(edgePath);
        //options.addArguments("--remote-debugging-port=9222"); // 使用不同的端口号
        driver = new EdgeDriver(options);
        //js = (JavascriptExecutor) driver;
        //action = new Actions(driver);
    }


    // todo 链接到特定浏览器窗口界面
    // todo xpath\js定位封装、事件触发封装、等待机制方法
    // todo 多frame切换问题
    // todo 滑块


    public void openUrl(String url){
        driver.get(url);
    }

    @Test
    public void test2() {
        // 设置 Chrome 浏览器驱动的路径
        System.setProperty("webdriver.chrome.driver", RPAConfig.envPath+ File.separator+ "drivers/chromedriver.exe");
        driver = new ChromeDriver();
        //js = (JavascriptExecutor) driver;
        //action = new Actions(driver);
    }
}