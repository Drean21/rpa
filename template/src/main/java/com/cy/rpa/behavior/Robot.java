package com.cy.rpa.behavior;

import com.cy.rpa.config.RPAConfig;
import com.cy.rpa.config.SikulixManage;
import com.cy.rpa.jna.JNAUtils;
import com.cy.rpa.toolkit.ThreadPool;
import com.sun.jna.platform.win32.WinDef;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.sikuli.basics.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * Roobot 机器人，模拟人的点击行为
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/09 14:19
 **/
@Data
@Slf4j
public class Robot {
    //鼠标移动时间
    private static Float moveTime = 1000F;

    //鼠标移动时间
    private static final int hz = 150;

    private static java.awt.Robot instence = null;


    /**
     * 桌面截图路径
     */
    private static final String winPicPath = RPAConfig.cachePath+File.separator + "win.png";


    static {
        String lagnguagePath = RPAConfig.envPath + File.separator + "lib";
        String lagnguage = "chi_sim";

        Settings.OcrDataPath = RPAConfig.envPath + File.separator + "lib";
        Settings.OcrLanguage = lagnguage;
    }

    /**
     * 获取Robot对象实例
     *
     * @return
     */
    public static java.awt.Robot getInstence() {
        if (instence == null) {
            synchronized (java.awt.Robot.class) {
                if (instence == null) {
                    try {
                        instence = new java.awt.Robot();
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instence;
    }

    /**
     * 通过Shift键控制滚动轴滚动，实现水平滚动
     *
     * @param wheel 控制滚动轴滚动的位移量
     */
    public static void scrollXShift(int wheel) {
        getInstence().keyPress(KeyEvent.VK_SHIFT);
        mouseWheels(wheel);
        getInstence().keyRelease(KeyEvent.VK_SHIFT);
    }

    /**
     * 通过Control键控制滚动轴滚动，实现缩放功能
     *
     * @param wheel 控制滚动轴滚动的位移量
     */
    public static void scrollXCtrl(int wheel) {
        getInstence().keyPress(KeyEvent.VK_CONTROL);
        mouseWheels(wheel);
        getInstence().keyRelease(KeyEvent.VK_CONTROL);
    }

    /**
     * 模拟一次回车键
     */
    public static void keyEnter() {
        ThreadPool.getInstance().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                getInstence().keyPress(KeyEvent.VK_ENTER);
                getInstence().delay(80);
                getInstence().keyRelease(KeyEvent.VK_ENTER);
                log.error("------已经出发enter键------");
            }
        }));
    }


    /**
     * 移动到某个位置点击一次,并等待几秒钟,
     *
     * @param x    屏幕横坐标
     * @param y    屏幕纵坐标
     */
    public static void mouseMoveAndClick(int x, int y) {
        mouseMove(x, y);
        sleep(200);
        mouseClick(1);
    }




    /**
     * 移动到某个位置点击一次
     *
     * @param x    屏幕横坐标
     * @param y    屏幕纵坐标
     * @return
     */
    public static boolean mouseMove(int x, int y) {
        try {
            boolean moveAccurate = false;
            getInstence().delay(100);
            int xReal = (int) MouseInfo.getPointerInfo().getLocation().getX();
            int yReal = (int) MouseInfo.getPointerInfo().getLocation().getY();
            for (int i = 0; i < 100; i++) {
                int x_ = ((x * i) / 100) + (xReal * (100 - i) / 100);
                int y_ = ((y * i) / 100) + (yReal * (100 - i) / 100);
                getInstence().mouseMove(x_, y_);
            }
            log.info("输入坐标:X=" + x + "-Y=" + y + ";实际坐标:x=" + MouseInfo.getPointerInfo().getLocation().getX() + ";y=" + MouseInfo.getPointerInfo().getLocation().getY());
            if (!moveAccurate) {
                log.error("鼠标移动失败,未到达指定区域...");
            } else {
                log.info("鼠标移动成功,到达指定区域....");
            }
            return moveAccurate;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移动到桌面几何中心位置
     */
    public static void moveToWindowCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Rectangle screenRectangle = new java.awt.Rectangle(screenSize);
        mouseMove((int) (screenRectangle.getX() + screenRectangle.getWidth() / 2), (int) (screenRectangle.getY() + screenRectangle.getHeight() / 2));
        log.info("鼠标移动到屏幕中央");
    }

    /**
     * 模拟粘贴动作 字符串粘贴
     *
     * @param string 要粘贴的文本
     */
    public static void setInputByPaste(String string) {
        log.info("粘贴文本::" + string);
        //声明一个StingSelection 对象，并使用String的参数完成实例化；
        StringSelection stringSelection = new StringSelection(string);
        //使用Toolkit对象的setContents将字符串放到粘贴板中 ；
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

        //判断操作系统
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("Mac")) {
            //按下crtl v键 ；
            getInstence().keyPress(KeyEvent.VK_META);
            getInstence().keyPress(KeyEvent.VK_V);
            //释放crtl v 键
            getInstence().keyRelease(KeyEvent.VK_V);
            getInstence().keyRelease(KeyEvent.VK_META);
        } else {
            //按下crtl v键 ；
            getInstence().keyPress(KeyEvent.VK_CONTROL);
            getInstence().keyPress(KeyEvent.VK_V);
            //释放crtl v 键
            getInstence().keyRelease(KeyEvent.VK_V);
            getInstence().keyRelease(KeyEvent.VK_CONTROL);
        }
        getInstence().delay(1000);
    }

    /**
     * 模拟输入动作 字符粘贴
     *
     * @param string 要粘贴的文本
     */
    public static void setInputByWriter(String string) {
        log.error("粘贴文本::" + string);
        if (StringUtils.isEmpty(string)) {
            return;
        }
        char[] chars = string.toCharArray();
        for (char aChar : chars) {
            //声明一个StingSelection 对象，并使用String的参数完成实例化；
            StringSelection stringSelection = new StringSelection(String.valueOf(aChar));
            //使用Toolkit对象的setContents将字符串放到粘贴板中 ；
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

            //判断操作系统
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("Mac")) {
                //按下crtl v键 ；
                getInstence().keyPress(KeyEvent.VK_META);
                getInstence().keyPress(KeyEvent.VK_V);
                //释放crtl v 键
                getInstence().keyRelease(KeyEvent.VK_V);
                getInstence().keyRelease(KeyEvent.VK_META);
            } else {
                //按下crtl v键 ；
                getInstence().keyPress(KeyEvent.VK_CONTROL);
                getInstence().keyPress(KeyEvent.VK_V);
                //释放crtl v 键
                getInstence().keyRelease(KeyEvent.VK_V);
                getInstence().keyRelease(KeyEvent.VK_CONTROL);
            }
            getInstence().delay(200);
        }
        getInstence().delay(500);
    }

    /**
     * 多键组合点击 例如 ctrl+s  ctrl+v
     * 默认休眠等待500毫秒
     *
     * @param key
     */
    public static void combinationClickKeys(int... key) {
        sleep(500);
        if (key.length == 0) {
            log.info("没有选择按键...");
            return;
        }

        try {
            log.info("组合点击 :");
            for (int i : key) {
                getInstence().keyPress(i);
                log.info(i + "; ");
                sleep(50);
            }
            sleep(100);
            log.info("   <----->    放开按键:");
            for (int i = key.length - 1; i >= 0; i--) {
                getInstence().keyRelease(key[i]);
                log.info(key[i] + "; ");
            }
        } catch (Exception e) {
            log.error("点击按钮失败:" + Arrays.toString(key) + "; 异常信息:" + e.getMessage());
        }
    }


    /**
     * ctrl+s 保存按钮
     */
    public static void save() {
        combinationClickKeys(KeyEvent.VK_CONTROL, KeyEvent.VK_S);
    }



    /**
     * 模拟点击键盘向左箭头键
     * @param number 按键次数
     */
    public static void keyLeft(int number) {
        for (int i = 0; i < number; i++) {
            combinationClickKeys(KeyEvent.VK_LEFT);
        }
        System.out.println("模拟按下向左箭头键：" + number + "次");
    }

    /**
     * 模拟点击键盘向右箭头键
     * @param number 按键次数
     */
    public static void keyRight(int number) {
        for (int i = 0; i < number; i++) {
            combinationClickKeys(KeyEvent.VK_RIGHT);
        }
        log.error("点击->按键次数:" + number);
    }

    /**
     * 模拟点击键盘向上箭头键
     * @param number 按键次数
     */
    public static void keyUp(int number) {
        for (int i = 0; i < number; i++) {
            combinationClickKeys(KeyEvent.VK_UP);
        }
        System.out.println("模拟按下向上箭头键：" + number + "次");
    }

    /**
     * 模拟点击键盘向下箭头键
     * @param number 按键次数
     */
    public static void keyDown(int number) {
        for (int i = 0; i < number; i++) {
            combinationClickKeys(KeyEvent.VK_DOWN);
        }
        System.out.println("模拟按下向下箭头键：" + number + "次");
    }

    /**
     * 模拟点击一次backspace键
     */
    public static void backSpace() {
        combinationClickKeys(KeyEvent.VK_BACK_SPACE);
        log.error("点击backspace::");
    }

    /**
     * 模拟点击一次backspace键
     *
     * @throws InterruptedException
     */
    public static void backSpaceCount(int count) {
        for (int i = 0; i < count; i++) {
            backSpace();
            sleep(200);
        }
        log.error(count + "次点击backspace::");
    }

    /**
     * 模拟点击 tab键
     *
     * @throws
     */
    public static void tab() {
        ThreadPool.getInstance().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                getInstence().keyPress(KeyEvent.VK_TAB);
                sleep(60);
                getInstence().keyRelease(KeyEvent.VK_TAB);
                log.error("点击tab键::");
            }
        }));
    }

    /**
     * 模拟点击 tab键
     *
     * @throws
     */
    public static void tab(int clickNum) {
        for (int i = 0; i < clickNum; i++) {
            tab();
        }
    }


    /**
     * 模拟点击鼠标左键
     *
     * @param j 点击次数
     */
    public static void mouseClick(int j) {
        try {
            for (int i = 0; i < j; i++) {
                getInstence().mousePress(InputEvent.BUTTON1_MASK);
                getInstence().delay(80);
                //鼠标释放
                getInstence().mouseRelease(InputEvent.BUTTON1_MASK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("鼠标点击失败");
        }
    }

    /**
     * 操作鼠标滚轮
     *
     * @param wheel 控制滚动的步数，正数表示向上滚动，负数表示向下滚动。
     */
    public static void mouseWheels(int wheel) {
        try {
            getInstence().mouseWheel(wheel);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("鼠标滚轮滚动失败");
        }
    }

    /**
     * 模拟鼠标右键点击操作
     */
    public static void mouseClickRight() {
        try {
            getInstence().mousePress(InputEvent.BUTTON3_MASK);
            getInstence().delay(80);
            getInstence().mouseRelease(InputEvent.BUTTON3_MASK);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("鼠标右击失败");
        }
    }


    /**
     * 截取屏幕上的指定区域并保存为图片文件
     * @param rectangle 截取的区域
     */
    public static String captureScreen(Rectangle rectangle) {
        try {
            String filename = RPAConfig.cachePath + File.separator + System.currentTimeMillis() + ".png";
            // 截取指定区域的屏幕截图
            BufferedImage screenshot = getInstence().createScreenCapture(rectangle);

            // 保存截图为图片文件
            ImageIO.write(screenshot, "png", new File(filename));

            System.out.println("截图成功，已保存为：" + filename);
            return filename;
        } catch (Exception e) {
            log.error("截屏失败:{}",e.getMessage());
            return "";
        }
    }

    /**
     * 截取屏幕上的指定区域并保存为图片文件
     * @param rectangle 截取的区域
     * @param filename 图片文件名
     */
    public static String captureScreen(Rectangle rectangle,String filename) {
        try {
            // 截取指定区域的屏幕截图
            BufferedImage screenshot = getInstence().createScreenCapture(rectangle);

            // 保存截图为图片文件
            ImageIO.write(screenshot, "png", new File(filename));

            System.out.println("截图成功，已保存为：" + filename);
            return filename;
        } catch (Exception e) {
            log.error("截屏失败:{}",e.getMessage());
            return "";
        }
    }


    // todo 存在问题，待解决（暂时搁置）
    /*------------------------------------基于Sikulix的GUI操作-----------------------------------------------*/

    /**
     * 模拟键盘输入文本的方法【目前只支持英文字符】
     *
     * @param text 需要输入的文本
     */
    public static void type(String text) {
        try {
            Screen screen = SikulixManage.getScreen();
            screen.type(text);
        } catch (Exception e) {
            log.error("sikuli type error", e.getMessage());
        }
    }

    /**
     * 模拟点击屏幕上具有特定图案的位置的方法
     *
     * @param btnPicPath 就是要点击的位置对应的图案路径
     * @return 如果成功根据图案进行点击返回true，如果找不到对应的图案或者发生其他异常则返回false
     */
    public static void clickByImg(String btnPicPath) {
        try {
            Screen screen = SikulixManage.getScreen();
            screen.click(btnPicPath);
        } catch (FindFailed e) {
            log.error("sikuli clickByImg error", e.getMessage());
        }
    }

    /**
     * 模拟点击特定图案的方法，但在点击前会等待一定的时间
     * @param btnPicPath 需要点击的图案路径
     * @param waitTime 点击前等待的时间（单位：秒）
     * @return 如果成功点击了指定图案，返回true，如果在等待时间内找不到图案或者发生其他异常，返回false
     */
    public static boolean clickByImg(String btnPicPath, int waitTime) {
        try {
            Screen screen = SikulixManage.getScreen();
            screen.wait(btnPicPath, waitTime);
            //screen.wait(1D);
            screen.click(btnPicPath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 模拟鼠标悬停在特定图案上的方法，悬停前会等待一定时间
     * @param btnPicPath 需要悬停的图案路径
     * @param waitTime 悬停前等待的时间（单位：秒）
     * @return 如果成功在指定图案上悬停，返回true，如果在等待时间内未找到图案或者发生其他异常，返回false
     */
    public static boolean hoverByImg(String btnPicPath, int waitTime) {
        try {
            Screen screen = SikulixManage.getScreen();
            screen.wait(btnPicPath, waitTime);
            screen.wait(1D);
            screen.hover(btnPicPath);
            return true;
        } catch (FindFailed findFailed) {
            return false;
        }
    }

    /**
     * 等待特定图案出现的方法
     * @param btnPicPath 需要等待出现的图案路径
     * @param waitTime 最多等待的时间（单位：秒）
     * @return 如果在指定时间内找到图案，返回true，如果无法在指定时间内找到图案或者发生其他异常，返回false
     */
    public static boolean waitByImg(String btnPicPath, int waitTime) {
        try {
            Screen screen = SikulixManage.getScreen();
            Match wait = screen.wait(btnPicPath, waitTime);
            if (wait != null) {
                return true;
            }
            return false;
        } catch (FindFailed findFailed) {
            return false;
        }
    }




    /**
     * 双击指定图片元素的方法
     * @param btnPicPath 需要双击的图片路径
     * @param waitTime 最多等待时间（单位：秒），在这段时间内如果找不到指定图片则会抛出异常
     * @return 如果成功找到并双击了指定图片，返回true，未找到或者出现异常，返回false
     */
    public static boolean doubleClick(String btnPicPath, int waitTime) {
        try {
            Screen screen = SikulixManage.getScreen();
            screen.wait(btnPicPath, waitTime);
            screen.doubleClick(btnPicPath);
            return true;
        } catch (FindFailed findFailed) {
            return false;
        }
    }


    /**
     * 把一个对象拖拽到另一个对象的方法
     * @param drog 需要拖拽的对象的图片路径
     * @param target 目标位置的图片路径
     * @param waitTime 最多等待时间（单位：秒），在这段时间内如果找不到指定图片则会抛出异常
     * @return 如果成功找到并拖拽了指定图片，返回true，未找到或者出现异常，返回false
     */
    public static boolean drog(String drog, String target, int waitTime) {
        try {
            Screen screen = SikulixManage.getScreen();
            screen.wait(drog, waitTime);
            screen.dragDrop(drog, target);
            return true;
        } catch (FindFailed findFailed) {
            return false;
        }
    }

    /**
     * 将目标对象沿x轴移动一定的距离
     * @param drog 需要拖拽的对象的图片路径
     * @param px 沿x轴需要移动的距离
     * @param waitTime 最多等待时间（单位：秒），在这段时间内如果找不到指定图片则会抛出异常
     * @return 如果成功找到并拖拽了指定图片，返回true，未找到或者出现异常，返回false
     * @throws FindFailed 抛出异常，若在尝试找到指定图片时出现错误
     */
    public static boolean drog(String drog, int px, int waitTime) {
        try {
            Screen screen = SikulixManage.getScreen();
            screen.wait(drog, waitTime);
            Match match = screen.find(drog);
            screen.dragDrop(match, new Location(match.getX() + px, 0));
            return true;
        } catch (FindFailed findFailed) {
            return false;
        }
    }


    /*------JNA功能区------------------------基于句柄的交互操作---------------------------------------------------*/

    /**
     * 文件下载指定绝对路径并保存
     *
     * @param savePath
     * @return
     */
    public static boolean specifyPathAndSaveFile(String savePath) {
        //等待窗体出现
        boolean saveed = false;
        try {
            saveed = specifyPathAndSaveFile(savePath, 35);

        } catch (Exception e) {
            log.error("JNA 保存文件失败;" + e.getMessage());
            e.printStackTrace();
        }

       /* if (!saveed) {
            try {
                setAndctrlVClipboardData(savePath);
                //保存文件
                doOneClickEnterKey();
                saveed = true;
                log.info("Robot 保存文件成功;");
            } catch (Exception e) {
                error("Robot 保存文件失败;" + e.getMessage());
            }
        }*/
        //清空win弹窗
        closeHandle(getWinRootElementByClassNameAndTitle(null, "另存为"));
        closeHandle(getWinRootElementByClassNameAndTitle("#32770", "确认另存为"));
        return saveed;
    }


    /**
     * 寻找并等待title为 "另存为" 的浏览器窗体句柄,指定文件保存路径,点击保存
     *
     * @param savePath
     * @param sceanTime
     * @return boolean
     */
    public static boolean specifyPathAndSaveFile(String savePath, int sceanTime) {
        long startTime = System.currentTimeMillis();
        //是否执行成功
        final boolean[] saveed = {false};
        //是否设置输入框内容失败
        boolean setError = false;
        final boolean[] runOver = {false};
        WinDef.HWND handle = waitGetWinRootElement(null, "另存为", sceanTime);
        if (handle != null) {
            if (setWinEditValue(handle, savePath)) {
                log.info("第一次文件路径写入成功...");
            } else {
                long countTime = System.currentTimeMillis();
                WinDef.HWND edit = waitGetWinElementInDesktop("Edit", null, 5);
                if (edit != null) {
                    JNAUtils.simulateClick(edit);
                    combinationClickKeys(KeyEvent.VK_CONTROL,KeyEvent.VK_A);
                    JNAUtils.simulateTextInput(edit, savePath);
                }else{
                    setError = true;
                }
                while (true) {
                    sleep(10);
                    if (System.currentTimeMillis() - countTime > 5 * 1000) {
                        setError = true;
                        break;
                    }
                    String winHWNDValue = getWinHWNDValue(edit);
                    if (savePath.equals(winHWNDValue)) {
                        setError = false;
                        log.info("文件路径写入成功:" + winHWNDValue);
                        break;
                    } else {
                        log.info("文件路径写入失败:" + winHWNDValue);
                    }
                }
            }
            sleep(500);
            WinDef.HWND finalHandle = handle;
            ThreadPool.getSingleExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    if (clickWinButton(finalHandle, "保存(&S)")) {
                        saveed[0] = true;
                        log.info("保存按钮点击成功...");
                    } else {
                        //doOneClickEnterKey();
                        keyEnter();
                        log.info("保存按钮点击失败...触发回车键");
                    }
                    runOver[0] = true;
                }
            });
            ThreadPool.getSingleExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    WinDef.HWND handle = waitGetWinRootElement("#32770", "确认另存为", 3);
                    if (handle != null) {
                        if (clickWinButton(handle, "是(&Y)")) {
                            saveed[0] = true;
                            log.info("保存确认按钮点击成功...");
                        } else {
                            log.info("保存按钮点击失败...");
                        }
                        runOver[0] = true;
                    }
                }
            });
        }

        while (!(saveed[0] || runOver[0])) {
            if (System.currentTimeMillis() - startTime > sceanTime * 1000) {
                break;
            }
            sleep(50);
        }

        if (saveed[0] && !setError) {
            System.out.println("保存完成,保存路径:" + savePath);
            return true;
        } else {
            System.out.println("保存失败,保存路径:" + savePath);
            return false;
        }
    }


    /**
     * 文件上传 指定绝对路径
     *
     * @param uploadFilePath
     * @return
     */
    public static boolean uploadFileByPath(String uploadFilePath) {
        sleep(2000);
        boolean saveed = false;
        try {
            uploadFileByPath(uploadFilePath, 25);
            saveed = true;
        } catch (Exception e) {
            log.error("JNA 上传文件失败;" + e.getMessage());
        }

        if (!saveed) {
            try {
                setInputByPaste(uploadFilePath);
                sleep(200);
                //保存文件
                keyEnter();
                saveed = true;
                log.info("Robot 上传文件成功;");
            } catch (Exception e) {
                log.error("Robot 上传文件失败;" + e.getMessage());
            }
        }
        return saveed;
    }

    /**
     * 文件上传 指定绝对路径
     *
     * @param uploadFilePath 文件绝对路径
     * @param sceanTime      扫描时间
     * @return boolean
     */
    public static boolean uploadFileByPath(String uploadFilePath, int sceanTime) {
        boolean saveed = false;
        WinDef.HWND handle = waitGetWinRootElement(null, "打开", sceanTime);
        if (handle != null) {
            WinDef.HWND edit = JNAUtils.findHandleByClassName("Edit", 10, TimeUnit.SECONDS, null);
            if (edit != null) {
                if (JNAUtils.simulateTextInput(edit, uploadFilePath)) {
                    log.info("上传文件地址添加成功");
                }else{
                    log.info("上传文件地址添加失败");
                }
                sleep(1000);
            }
            //WinDef.HWND finalHandle = handle;
            WinDef.HWND saveButton = JNAUtils.findHandleByClassName("Button", 10, TimeUnit.SECONDS, "打开(&O)");
            if (saveButton != null) {
                JNAUtils.simulateClick(saveButton);
            }
            System.out.println("上传的文件路径为:" + uploadFilePath);
            saveed = true;
        }
        return saveed;
    }



    /**--------------------------------------获取window桌面上任意句柄的方法------------------------------------------*/

    /**
     * 等待window元素出现,从桌面寻找,桌面上没有则返回false,适用于应用程序内部的桌面子类句柄中的最顶层(图层)句柄
     *
     * @param className 句柄类名
     * @param title     标题
     * @param sceanTime 扫描时间
     * @return
     */
    public boolean waitFindWinElementInDesktop(String className, String title, int sceanTime) {
        long stopTime = System.currentTimeMillis() + sceanTime * 1000;
        while (System.currentTimeMillis() < stopTime) {
            sleep(100);
            if (JNAUtils.findHandleByClassName(null, className, title).size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取window元素,从桌面寻找,桌面上没有则返回空,适用于应用程序内部,桌面最顶层句柄
     *
     * @param className 句柄类名
     * @param title     标题
     * @param sceanTime 扫描时间
     * @return
     */
    public static WinDef.HWND waitGetWinElementInDesktop(String className, String title, int sceanTime) {
        long stopTime = System.currentTimeMillis() + sceanTime * 1000;
        while (System.currentTimeMillis() < stopTime) {
            sleep(100);
            WinDef.HWND hwnd = getWinElementByClassAndTitle(className, title);
            if (hwnd != null) {
                return hwnd;
            }
        }
        return null;
    }

    /**
     * 等待window元素消失
     *
     * @param className 句柄类名
     * @param title     标题
     * @param sceanTime 扫描时间
     * @return
     */
    public boolean waitWinElementDisappear(String className, String title, int sceanTime) {
        sleep(1000);
        long stopTime = System.currentTimeMillis() + sceanTime * 1000;
        while (System.currentTimeMillis() < stopTime) {
            sleep(500);
            if (JNAUtils.findHandleByClassNameAndCaption(className, title) == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据句柄类名和标题获取句柄
     *
     * @param className 句柄的class类名
     * @param title     句柄标题
     * @return
     */
    public static WinDef.HWND getWinElementByClassAndTitle(String className, String title) {
        return JNAUtils.findHandleByClassNameAndCaption(className, title);
    }

    /**
     * 根据顶层句柄类名和标题获取句柄集合
     *
     * @param className
     * @param title
     * @return
     */
    public List<WinDef.HWND> getWinElementsByClassAndTitle(String className, String title) {
        return JNAUtils.findHandleByClassName(null, className, title);
    }


    /**--------------------------------------获取window桌面上任意句柄的方法------------------------------------------*/

    /**----------------------------操作window顶层(非子类句柄,比如浏览器另存为窗体句柄)句柄的方法 开始-------------------------------*/

    /**
     * 等待window元素出现(获取元素的顶层元素),并返回(默认等待时间为10秒)
     *
     * @param className 句柄类名
     * @param title     句柄标题
     * @return
     */
    public WinDef.HWND waitGetWinRootElement(String className, String title) {
        return waitGetWinRootElement(className, title, 10);
    }


    /**
     * 等待window元素(获取元素的顶层元素)出现,并返回
     *
     * @param className 句柄类名
     * @param title     句柄标题
     * @param sceanTime 扫描时间
     * @return
     */
    public static WinDef.HWND waitGetWinRootElement(String className, String title, int sceanTime) {
        long stopTime = System.currentTimeMillis() + sceanTime * 1000;
        while (System.currentTimeMillis() < stopTime) {
            sleep(500);
            WinDef.HWND hwnd = getWinRootElementByClassNameAndTitle(className, title);
            if (hwnd != null) {
                return hwnd;
            }
        }
        return null;
    }

    /**
     * 等待window元素消失
     *
     * @param className 句柄类名
     * @param title     标题
     * @param sceanTime 扫描时间
     * @return
     */
    public boolean waitWinRootElementDisappear(String className, String title, int sceanTime) {
        sleep(1000);
        long stopTime = System.currentTimeMillis() + sceanTime * 1000;
        while (System.currentTimeMillis() < stopTime) {
            sleep(500);
            if (!findWinRootElementByClassNameAndTitle(className, title)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 等待10秒内window元素出现,(获取元素的顶层元素)
     *
     * @param className 句柄类名
     * @param title     标题/其他条件
     * @return
     */
    public boolean waitFindWinRootElement(String className, String title) {
        return waitFindWinRootElement(className, title, 10);
    }


    /**
     * 等待window元素出现(获取元素的顶层元素)
     *
     * @param className 句柄类名
     * @param title     标题
     * @param sceanTime 扫描时间
     * @return
     */
    public boolean waitFindWinRootElement(String className, String title, int sceanTime) {
        long stopTime = System.currentTimeMillis() + sceanTime * 1000;

        while (System.currentTimeMillis() < stopTime) {
            sleep(1000);
            if (findWinRootElementByClassNameAndTitle(className, title)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据window句柄的类名和标题获取window句柄 (获取桌面的顶层元素)
     *
     * @param className window句柄类名
     * @param title     win到我句柄标题
     * @return 句柄对象
     */
    public static WinDef.HWND getWinRootElementByClassNameAndTitle(String className, String title) {
        WinDef.HWND handle = JNAUtils.findHandleByClassNameAndTitle(className, title);
        return handle;
    }

    /**
     * 根据window句柄的类名和标题获取window句柄
     * 获取元素的顶层元素
     *
     * @param className window句柄类名
     * @param title     win到我句柄标题
     * @return 是否存在
     */
    public boolean findWinRootElementByClassNameAndTitle(String className, String title) {
        WinDef.HWND handle = JNAUtils.findHandleByClassNameAndTitle(className, title);
        if (handle == null) {
            return false;
        } else {
            return true;
        }
    }

    /**----------------------------操作window顶层句柄的方法 结束-------------------------------*/


    /**
     * 点击指定句柄下的window 按钮
     *
     * @param caption 筛选条件:title ,aaname,等
     * @return 点击成功返回true 否则返回false
     */
    public boolean clickWinButton(String className, String caption) {
        if (className != null) {
            try {
                WinDef.HWND okButton = JNAUtils.findHandleByClassName(className, 10, TimeUnit.SECONDS, caption);
                if (okButton != null) {
                    JNAUtils.simulateClick(okButton);
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 点击指定句柄下的window 按钮
     *
     * @param index 筛选条件:title ,aaname,等
     * @return 点击成功返回true 否则返回false
     */
    public boolean clickWinButton(String className, int index) {
        if (className != null) {
            try {
                WinDef.HWND okButton = JNAUtils.findHandleByClassName(className, index);
                if (okButton != null) {
                    JNAUtils.simulateClick(okButton);
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 点击指定句柄下的window 按钮
     *
     * @param handle  父级句柄
     * @param caption 筛选条件:title ,aaname,等
     * @return 点击成功返回true 否则返回false
     */
    public static boolean clickWinButton(WinDef.HWND handle, String caption) {
        return clickWinButton(handle, "Button", caption);
    }

    /**
     * 点击指定句柄下的window 按钮
     *
     * @param handle  父级句柄
     * @param caption 筛选条件:title ,aaname,等
     * @return 点击成功返回true 否则返回false
     */
    public static boolean clickWinButton(WinDef.HWND handle, String className, String caption) {
        try {
            WinDef.HWND okButton = JNAUtils.findHandleByClassName(handle, className, 10, TimeUnit.SECONDS, caption);
            if (okButton != null) {
                JNAUtils.simulateClick(okButton);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 设置指定句柄下window输入框的值
     *
     * @param handle 父级句柄
     * @param text   输入文本
     * @return
     */
    public static boolean setWinEditValue(WinDef.HWND handle, String text) {
        return setWinEditValue(handle, "Edit", null, text);
    }

    /**
     * 设置指定句柄下window输入框的值,指定按钮的title
     *
     * @param handle  父级句柄
     * @param caption 筛选条件 title等参数
     * @param text    输入文本
     * @return
     */
    public static boolean setWinEditValue(WinDef.HWND handle, String className, String caption, String text) {
        if (handle != null) {
            try {
                WinDef.HWND edit = JNAUtils.findHandleByClassName(handle, className, 10, TimeUnit.SECONDS, caption);
                if (edit != null) {
                    JNAUtils.simulateTextInput(edit, text);
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 设置指定句柄下window输入框的值
     *
     * @param handle  父级句柄
     * @param caption 筛选条件 title等参数
     * @return
     */
    public String getWinEditValue(WinDef.HWND handle, String caption) {
        if (handle != null) {
            try {
                WinDef.HWND edit = JNAUtils.findHandleByClassName("Edit", 10, TimeUnit.SECONDS, caption);
                if (edit != null) {
                    String winHandleText = JNAUtils.getWinHandleText(edit);
                    return winHandleText;
                }
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    /**
     * 设置指定句柄下window输入框的值
     *
     * @param className 类名
     * @param index     索引
     * @return
     */
    public String getWinEditValue(String className, int index) {
        try {
            WinDef.HWND edit = JNAUtils.findHandleByClassName(className, index);
            if (edit != null) {
                String winHandleText = JNAUtils.getWinHandleText(edit);
                return winHandleText;
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 设置指定句柄下window输入框的值
     *
     * @param handle 类名
     * @return
     */
    public static String getWinHWNDValue(WinDef.HWND handle) {
        if (handle == null) {
            return "";
        }
        try {
            String text = JNAUtils.getWinHandleText(handle);
            return text;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取指定句柄在桌面上的区域 左上角 x,y,width,hight 多用于截图
     *
     * @param handle
     * @return
     */
    public Rectangle getWinHWNDRect(WinDef.HWND handle) {
        WinDef.RECT rect = JNAUtils.getWinElementRect(handle);
        Rectangle rectangle = new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
        return rectangle;
    }


    /**
     * 根据类名关闭指定的句柄
     *
     * @param className
     * @return
     */
    public boolean closeHandleByClassName(String className) {
        return JNAUtils.closeHandleByClassName(className);
    }

    /**
     * 关闭指定的句柄
     *
     * @param hwnd 句柄对象
     * @return
     */
    public static boolean closeHandle(WinDef.HWND hwnd) {
        return JNAUtils.closeHandle(hwnd);
    }


    /**
     * 获取句柄元素中心点坐标
     *
     * @param handle
     * @return
     */
    public java.awt.Point clickWinHWNDByRobot(WinDef.HWND handle) {
        Rectangle rect = getWinHWNDRect(handle);
        java.awt.Point point = new java.awt.Point();
        mouseMoveAndClick((int) (rect.getX() + (rect.getWidth() / 2)), (int) (rect.getY() + (rect.getHeight() / 2)));
        return point;
    }
}