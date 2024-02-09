package com.cy.rpa.behavior;

import com.cy.rpa.RPAConfig;
import com.cy.toolkit.ThreadPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

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
    public static void ClickEnterKey() {
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
        clickMouse(1);
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
    public static void clickMouse(int j) {
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
    public static void rightMouse() {
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
}