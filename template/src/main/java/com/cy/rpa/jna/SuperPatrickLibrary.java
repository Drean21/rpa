package com.cy.rpa.jna;

import com.sun.jna.Library;

/**
 * SuperPatrick 操作Api;方法中参数需要使用superPatrick采集工具进行节点采集
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/09 22:05
 */
public interface SuperPatrickLibrary extends Library {
    /**
     * 寻找并点击元素
     *
     * @param pStrId        元素id
     * @param pStrName      元素标题,名称
     * @param pStrClassName 元素类名,classname
     * @param controlType   控件类型
     */
    boolean findElement(String pStrId, String pStrName, String pStrClassName, String controlType);

    /**
     * @param pKeysString 需要输入的文本
     */
    void sendKeys(String pKeysString);

    /**
     * @param pKeysString 输入键盘按键  {win}r {alt}a
     */
    void sendShortCutKeys(String pKeysString);

    /**
     * 全屏扫描点击图像
     *
     * @param strPartImage 按钮图像路径
     * @return
     */
    boolean findImage(String strPartImage);

    /**
     * 点击图像
     *
     * @param strPartImage 按钮图像路径
     * @param a            左上角x
     * @param b            左上角Y
     * @param c            右下角x
     * @param d            右下角y
     * @return
     */
    boolean findStaticImage(String strPartImage, int a, int b, int c, int d);

    /**
     * 通过元素类名,标题和图像点击元素
     *
     * @param strClass     元素类名
     * @param strTitle     元素标题
     * @param strPartImage 按钮的图像路径
     * @return
     */
    boolean findImageByHwnd(String strClass, String strTitle, String strPartImage);
}
