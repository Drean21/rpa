package com.cy.web;

import cn.hutool.core.io.FileUtil;
import com.cy.rpa.RPAConfig;
import lombok.Data;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

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

    /* 等待器 */
    WebDriverWait wait;


    /**
     *初始化内置的谷歌浏览器
     */
    public void initChrome() {
        try {
            // 设置 ChromeDriver 路径
            String chromeDriverPath = RPAConfig.envPath + File.separator + "browser/drivers/chromedriver.exe";
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            // 设置 Chrome 可执行文件路径
            String chromePath = RPAConfig.envPath + File.separator + "browser/chrome-win64/chrome.exe";
            ChromeOptions options = new ChromeOptions();
            options.setBinary(chromePath);

            // 添加其他 ChromeOptions 设置（可根据需要自行添加）
             options.addArguments("--start-maximized"); // 最大化窗口
            // options.addArguments("--headless"); // 无头模式
            options.addArguments("--remote-allow-origins=*");//解决 403 出错问题

            // 创建 ChromeDriver 实例
            driver = new ChromeDriver(options);
            action = new Actions(driver);
            js = (JavascriptExecutor) driver;
            wait= new WebDriverWait(driver,  Duration.ofSeconds(10));
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            // 可添加其他处理逻辑，如日志记录、报警等
        }
    }

    /**
     * 关闭浏览器
     */
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }



    // todo 链接到特定浏览器窗口界面
    // todo xpath\js定位封装、事件触发封装、等待机制方法
    // todo 多frame切换问题
    // todo 滑块


    /**
     * 打开网页
     * @param url
     */
    public void openUrl(String url){
        if (driver == null) {
            initChrome();
        }
        driver.get(url);
    }


    /**
     * 网页元素截屏
     * @param  by  the locating mechanism for the web element
     * @return     the file path of the captured screenshot
     */
    public String captureElementScreenshot(By by) {
        String path = RPAConfig.cachePath + File.separator + System.currentTimeMillis() + ".png";
        try {
            // 定位要截图的元素，可以使用元素的XPath、CSS选择器等方法
            WebElement element = getWebElement(by);
            // 截取指定元素的截图
            File screenshot = ((TakesScreenshot) element).getScreenshotAs(OutputType.FILE);
            //FileUtils.copyFile(screenshot, new File("ele  ment_screenshot.png"));
            FileUtil.copyFile(screenshot, new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }



    /**
     * 点击元素
     * @param by
     */
    public void clickElement(By by){
        //getWebElement(by).click();
        action.click(getWebElement(by)).perform();
    }

    /**
     * 设置输入框元素的值
     * @param by
     * @param value
     */
    public void setInputValue(By by, String value){
        WebElement element = getWebElement(by);
        try {
            action.sendKeys(element,value).perform();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

    /**
     * 创建新的浏览器对象
     * @return the WebDriver instance created
     */
    public WebWorker newChrome(){
        WebWorker webWorker = new WebWorker();
        webWorker.initChrome();
        return webWorker;
    }


    /**
     * 选择下拉菜单中的选项 
     */
    public void selectOptionInDropdown(By by, String optionText) {
        WebElement element = getWebElement(by);
        if (isSelectElement(element)) {
            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(optionText);
        } else {
            throw new IllegalArgumentException("元素不是下拉菜单类型");
        }
    }


    /**
     * 判断元素是否是下拉菜单类型
     * @param element
     * @return
     */
    private boolean isSelectElement(WebElement element) {
        return element.getTagName().equalsIgnoreCase("select");
    }


    /**
     * 获取网页元素对象
     * @param  by  the locating mechanism
     * @return     the web element identified by the given By object
     */
    private WebElement getWebElement(By by) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return element;
    }
}