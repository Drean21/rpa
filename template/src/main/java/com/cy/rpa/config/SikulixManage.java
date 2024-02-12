package com.cy.rpa.config;

import cn.hutool.core.util.ObjectUtil;
import com.cy.rpa.RPAConfig;
import org.sikuli.basics.Settings;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.sikuli.script.TextRecognizer;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 封装使用SikuliX库进行图形用户界面自动化的一些常用方法和策略
 */
public class SikulixManage {
    static {
        Settings.OcrDataPath = RPAConfig.envPath+ File.separator+"lib";
        Settings.OcrLanguage = "chi_sim";
    }
    /**
     * screen对象调用次数计数器
     */
    private static AtomicInteger screenUsCount = new AtomicInteger(0);
    /**
     * pattern对象调用次数计数器
     */
    private static AtomicInteger patternUsCount = new AtomicInteger(0);
    /**
     * 调用次数
     */
    private static final int SCREEN_MAX_GET_COUNT = 6;


    /**
     * 调用次数
     */
    private static final int PATTERN_MAX_GET_COUNT = 6;

    /**
     * sikilix screend对象用于识别发现按钮
     */
    public static Screen screen = getScreen();

    /**
     * sikilix pattern对象用匹配发现桌面元素
     */
    public static Pattern pattern = getPattern();

    /**
     * Sikuli的类Screen
     * Screen类是Sikuli提供的所有方法的基类。Screen类包含用于屏幕元素上所有常用操作的预定义方法，如单击、双击、向文本框提供输入、悬停等。下面是Screen类提供的常用方法列表
     * ---------------------------------------------------------------------------------------
     * Method	     |    Description	   |                             Syntax(语法)
     * --------------------------------------------------------------------------------------
     * Click         |       单击	       |     此方法用于使用图像名称作为参数单击屏幕上的元素。
     * |                     |      Screen s = new Screen(); s.click(“QA.png”);
     * ---------------------------------------------------------------------------------------
     * doubleClick   |      双击	           |     此方法用于双击元素。它接受图像名称作为参数。
     * |                     |     Screen s = new Screen(); s.doubleClick(“QA.png”);
     * ---------------------------------------------------------------------------------------
     * Type          |      输入	           |     此方法用于向元素提供输入值。它接受图像名称和文本作为参数。
     * |                     |               s.type(“QA.png”,“TEXT”);
     * ---------------------------------------------------------------------------------------
     * Hover         |      悬停	           |     此方法用于将鼠标悬停在元素上。 它接受图像名称作为参数。
     * |                     |                   s.hover(“QA.png”);
     * ---------------------------------------------------------------------------------------
     * Find          |      查找	           |     此方法用于在屏幕上查找特定元素。 它接受图像名称作为参数。
     * |                     |                    s.find(“QA.png”);
     * ----------------------------------------------------------------------------------------
     */
    public static Screen getScreen() {
        if (screenUsCount.incrementAndGet() > SCREEN_MAX_GET_COUNT || ObjectUtil.isEmpty(screen)) {
            screen = new Screen();
            screenUsCount.set(0);
        }
        return screen;
    }


    /**
     * Sikuli的类Pattern
     * Pattern类用于将图像文件与其他属性相关联，以唯一标识元素。它将图像的路径作为参数。
     * Pattern p = new Pattern(“Path of image”);
     * 下面是模式类最常用的方法。
     * -----------------------------------------------------------
     * Method	            |        Description	Syntax(语法)
     * -----------------------------------------------------------
     * getFileName	    |    返回Pattern 对象中包含的文件名。
     * |    Pattern p = new Pattern(“D:\Demo\QA.png”); String filename = p.getFileName();
     * ----------------------------------------------------------
     * similar	        |     此方法返回一个新的Pattern对象，其相似性设置为指定值。 它接受0到1之间的相似性值作为参数。
     * |    Sikuli查找属于指定相似范围的所有元素并返回一个新的模式对象。	Pattern p1 = p.similar(0.7f);
     * -----------------------------------------------------------
     * Exact	        |      该方法返回一个新的Pattern对象，相似度设置为1。它只查找指定元素的精确匹配。
     * |        Pattern p1 = p.exact();
     * ————————————————-------------------------------------------
     */
    public static Pattern getPattern() {
        if (patternUsCount.incrementAndGet() > PATTERN_MAX_GET_COUNT || ObjectUtil.isEmpty(pattern)) {
            pattern = new Pattern();
            patternUsCount.set(0);
        }
        return pattern;
    }

    /**
     * 获取TextRecognizer对象
     * @return
     */
    public static TextRecognizer getTextRecognizer(){
        TextRecognizer start = TextRecognizer.start();
        return start;
    }

}
