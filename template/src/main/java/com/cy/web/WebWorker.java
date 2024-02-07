package com.cy.web;

import cn.hutool.core.io.FileUtil;
import com.cy.rpa.RPAConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * WebWorker 网页自动化根类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/01 19:07
 **/
@Data
@Slf4j
public class WebWorker {

    /*浏览器驱动*/
    private WebDriver driver;
    /*js执行器*/
    private JavascriptExecutor js;

    /*Action类 链式网页操作*/
    private Actions action;

    /**
     * 初始化谷歌浏览器
     */
    public void initChrome() {
        try {
            //设置 ChromeDriver 路径
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
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            log.warn("路径下内置的谷歌浏览器驱动不存在！正在尝试使用WebDriverManager获取驱动...");
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            // 添加其他 ChromeOptions 设置（可根据需要自行添加）
            options.addArguments("--start-maximized"); // 最大化窗口
            // options.addArguments("--headless"); // 无头模式
            options.addArguments("--remote-allow-origins=*");//解决 403 出错问题
            // 创建 ChromeDriver 实例
            driver = new ChromeDriver(options);
            action = new Actions(driver);
            js = (JavascriptExecutor) driver;
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
     *
     * @param url
     */
    public void openUrl(String url) {
        if (driver == null) {
            initChrome();
        }
        driver.get(url);
    }

    /**
     * 在新标签页中打开网页
     *
     * @param url 要打开的网页URL
     */
    public void openUrlInNewTab(String url) {
        if (driver == null) {
            initChrome();
        }
        // 打开一个新标签页并切换到该标签页
        newTab();
        // 在新标签页中打开网页
        driver.get(url);
    }


    /**
     * 网页元素截屏
     *
     * @param by the locating mechanism for the web element
     * @return the file path of the captured screenshot
     */
    public String captureElementScreenshot(By by) {
        String path = RPAConfig.cachePath + File.separator + System.currentTimeMillis() + ".png";
        try {
            // 定位要截图的元素，可以使用元素的XPath、CSS选择器等方法
            WebElement element = getElement(by);
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
     *
     * @param by
     */
    public void clickElement(By by) {
        //getElement(by).click();
        action.click(getElement(by)).perform();
    }

    /**
     * 设置输入框元素的值
     *
     * @param by
     * @param value
     */
    public void setInputValue(By by, String value) {
        WebElement element = getElement(by);
        try {
            action.sendKeys(element, value).perform();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建新的浏览器对象
     *
     * @return the WebDriver instance created
     */
    public WebWorker newChrome() {
        WebWorker webWorker = new WebWorker();
        webWorker.initChrome();
        return webWorker;
    }


    /**
     * 选择下拉菜单中的选项 （对于非select标签，模拟两次元素点击即可）
     */
    public void selectOptionInDropdown(By by, String optionText) {
        WebElement element = getElement(by);
        if (isSelectElement(element)) {
            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(optionText);
        } else {
            throw new IllegalArgumentException("元素不是下拉菜单类型");
        }
    }


    /**
     * 判断元素是否是下拉菜单类型
     *
     * @param element
     * @return
     */
    private boolean isSelectElement(WebElement element) {
        return element.getTagName().equalsIgnoreCase("select");
    }


    /**
     * 获取网页元素对象(默认等待10s)
     *
     * @param by the locating mechanism
     * @return the web element identified by the given By object
     */
    private WebElement getElement(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return element;
    }

    /**
     * 获取多个网页元素对象列表(默认等待10s)
     *
     * @param by the locating mechanism
     * @return the list of web elements identified by the given By object
     */
    private List<WebElement> getElements(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
        return elements;
    }

    /**
     * 获取网页元素对象(指定等待时长)
     *
     * @param by   the locating mechanism
     * @param time the time to wait in seconds
     * @return the located WebElement
     */
    private WebElement getElement(By by, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return element;
    }

    /**
     * 获取多个网页元素对象列表(指定等待时长)
     *
     * @param by the locating mechanism
     * @return the list of web elements identified by the given By object
     */
    private List<WebElement> getElements(By by, int time) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
        return elements;
    }

    /**
     * 切换到指定标签的Frame
     *
     * @param FrameFlag name,id
     * @return
     */
    public boolean switchToFrame(String FrameFlag) {
        try {
            driver.switchTo().frame(FrameFlag);
            return true;
        } catch (Exception e) {
            log.error("iframe切换失败,原因：%s", e.getMessage());
            return false;
        }
    }

    /**
     * 切换到指定的Frame
     *
     * @param FrameIndex Frame的顺序
     * @return
     */
    public boolean switchToFrame(int FrameIndex) {
        try {
            driver.switchTo().frame(FrameIndex);
            return true;
        } catch (Exception e) {
            log.error("iframe切换失败,原因：%s", e.getMessage());
            return false;
        }
    }

    /**
     * 切换到指定的Frame
     *
     * @param FrameElement Frame窗体
     * @return
     */
    public boolean switchToFrame(WebElement FrameElement) {
        try {
            driver.switchTo().frame(FrameElement);
            return true;
        } catch (Exception e) {
            log.error("iframe切换失败,原因：%s", e.getMessage());
            return false;
        }
    }

    /**
     * 切换到父级Frame
     *
     * @return
     */
    public boolean switchToParentFrame() {
        try {
            driver.switchTo().parentFrame();
            return true;
        } catch (Exception e) {
            log.error("iframe切换失败,原因：%s", e.getMessage());
            return false;
        }
    }

    /**
     * 切换顶层容器:最外层的html内部
     *
     * @return
     */
    public boolean switchToDefaultContent() {
        try {
            driver.switchTo().defaultContent();
            return true;
        } catch (Exception e) {
            log.error("iframe切换失败,原因：%s", e.getMessage());
            return false;
        }
    }


    /**
     * 打开新的标签页
     */
    public void newTab() {
        js.executeScript("window.open();");
        switchToNewTab();
    }

    /**
     * 关闭当前标签页
     */
    public void closeTab() {
        js.executeScript("window.close();");
    }

    /**
     * 关闭指定标签页
     *
     * @param tabName  要关闭的标签页名称
     */
    public boolean closeTabByTitle(String tabName) {
        // 记录当前窗口句柄，用于在切换失败时恢复到原来的窗口
        String currentHandle = driver.getWindowHandle();
        // 获取当前浏览器的所有窗口句柄
        Set<String> handles = driver.getWindowHandles();
        // 遍历窗口句柄
        Iterator<String> it = handles.iterator();
        while (it.hasNext()) {
            String handle = it.next();
            try {
                driver.switchTo().window(handle); // 切换到窗口
                String title = driver.getTitle(); // 获取窗口标题
                // 如果标题与指定的标签页名称匹配，则执行关闭操作
                if (title.equals(tabName)) {
                    driver.close(); // 关闭标签页
                    return true;
                }
            } catch (Exception e) {
                log.error("寻找标签页出现异常：" + e.getMessage());
            }
        }
        // 切换失败，恢复到原来的窗口
        driver.switchTo().window(currentHandle);
        log.info("未找到指定标题的标签页");
        return false;
    }

    /**
     * 关闭具有指定URL前缀的标签页
     *
     * @param urlPrefix 要关闭的标签页URL前缀
     */
    public boolean closeTabByUrlPrefix(String urlPrefix) {
        // 记录当前窗口句柄，用于在切换失败时恢复到原来的窗口
        String currentHandle = driver.getWindowHandle();
        // 获取当前浏览器的所有窗口句柄
        Set<String> handles = driver.getWindowHandles();
        // 遍历窗口句柄
        Iterator<String> it = handles.iterator();
        while (it.hasNext()) {
            String handle = it.next();
            try {
                driver.switchTo().window(handle); // 切换到窗口
                String currentUrl = driver.getCurrentUrl(); // 获取当前窗口的URL
                // 如果当前URL以指定前缀开头，则执行关闭操作
                if (currentUrl.startsWith(urlPrefix)) {
                    driver.close(); // 关闭标签页
                    return true;
                }
            } catch (Exception e) {
                log.error("寻找标签页出现异常：" + e.getMessage());
            }
        }
        // 切换失败，恢复到原来的窗口
        driver.switchTo().window(currentHandle);
        log.info("未找到具有指定URL前缀的标签页");
        return false;
    }

    /**
     * 关闭除当前标签页以外的所有标签页
     */
    public void closeAllTabsExceptCurrent() {
        // 获取当前窗口句柄
        String currentHandle = driver.getWindowHandle();
        try {
            // 获取所有窗口句柄
            Set<String> handles = driver.getWindowHandles();
            // 遍历窗口句柄
            for (String handle : handles) {
                // 如果不是当前窗口句柄，则关闭该窗口
                if (!handle.equals(currentHandle)) {
                    driver.switchTo().window(handle); // 切换到该窗口
                    driver.close(); // 关闭窗口
                }
            }
            // 切换回当前窗口句柄
            driver.switchTo().window(currentHandle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 切换到新打开的标签页
     */
    private void switchToNewTab() {
        // 获取所有窗口的句柄
        for (String handle : driver.getWindowHandles()) {
            // 切换到新标签页的句柄
            driver.switchTo().window(handle);
        }
    }
    /**
     * 切换到指定标题的标签页
     * @param tabTitle 标签页标题
     */
    public Boolean switchTabByTitle(String tabTitle) {
        //js.executeScript("window.switch('" + tabName + "');");
        if (StringUtils.isEmpty(tabTitle)) {
            return false;
        }
        if (getCurrentPageTitle().contains(tabTitle)) {
            return true;
        }
        //获取当前浏览器的所有窗口句柄
        Set<String> handles = driver.getWindowHandles();
        // 记录当前窗口句柄，用于在切换失败时恢复到原来的窗口
        String currentHandle = driver.getWindowHandle();
        Iterator<String> it = handles.iterator();
        while (it.hasNext()) {
            String next = it.next();
            try {
                driver.switchTo().window(next);//切换到新窗口
                //initBrowserZoom();
                if (getCurrentPageTitle().contains(tabTitle)) {
                    log.info("切换到标签页(title):"+driver.getTitle());
                    return true;
                }
            } catch (Exception e) {
                log.info("寻找标签页出现异常:" + e.getMessage());
            }
        }
        // 切换失败，恢复到原来的窗口
        driver.switchTo().window(currentHandle);
        log.info("未找到指定标题的标签页");
        return false;
    }

    /**
     * 获取当前页的标题
     *
     * @return
     */
    public String getCurrentPageTitle() {
        String title = driver.getTitle();
        return StringUtils.isNotBlank(title) ? title : "null";
    }

    /**
     * 获取当前页URL
     *
     * @return
     */
    public String getCurrentPageUrl() {
        String url = driver.getCurrentUrl();
        return StringUtils.isNotBlank(url) ? url : "null";
    }

    /**
     * 切换到具备特定元素的窗体
     *
     * @param by
     */
    public boolean switchTabByElement(By by) {
        // 记录当前窗口句柄，用于在切换失败时恢复到原来的窗口
        String currentHandle = driver.getWindowHandle();
        try {
            if (getElement(by, 2) != null) {
                return true;
            }
            //获取当前浏览器的所有窗口句柄
            Set<String> handles = driver.getWindowHandles();//获取所有窗口句柄
            Iterator<String> it = handles.iterator();
            while (it.hasNext()) {
                String next = it.next();
                try {
                    driver.switchTo().window(next);//切换到新窗口
                    //initBrowserZoom();
                    if (checkElement(by)) {
                        return true;
                    }
                } catch (Exception e) {
                    log.error("寻找标签页出现异常：" + next);
                }
            }
        } catch (Exception e) {
            log.error("窗体切换失败!!!!");
            return false;
        }
        // 切换失败，恢复到原来的窗口
        driver.switchTo().window(currentHandle);
        log.info("未找到具有指定URL前缀的标签页");
        return false;
    }
    /**
     * 切换到具有指定URL前缀的标签页
     *
     * @param urlPrefix URL前缀
     * @return 如果成功切换到具有指定URL前缀的标签页，则返回true；否则返回false
     */
    public boolean switchTabByUrlPrefix(String urlPrefix) {
        // 获取当前浏览器的所有窗口句柄
        Set<String> handles = driver.getWindowHandles();
        // 记录当前窗口句柄，用于在切换失败时恢复到原来的窗口
        String currentHandle = driver.getWindowHandle();
        // 遍历窗口句柄
        for (String handle : handles) {
            try {
                driver.switchTo().window(handle); // 切换到窗口
                String currentUrl = driver.getCurrentUrl(); // 获取当前窗口的URL
                // 如果当前URL以指定前缀开头，则返回true
                if (currentUrl.startsWith(urlPrefix)) {
                    return true;
                }
            } catch (Exception e) {
                log.error("寻找标签页出现异常：" + e.getMessage());
            }
        }
        // 切换失败，恢复到原来的窗口
        driver.switchTo().window(currentHandle);
        log.info("未找到具有指定URL前缀的标签页");
        return false;
    }

    private boolean checkElement(By seletor) {
        try {
            sleep(200);
            driver.findElement(seletor);
            log.info("checkElement -> true:" + seletor.toString());
            return true;
        } catch (Exception e) {
            log.info("checkElement -> false:" + seletor.toString());
            return false;
        }
    }


}