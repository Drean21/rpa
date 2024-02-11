package com.cy.rpa.jna;

/**
 *  Windows消息常量
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/09 22:05
 */
public class Win32MessageConstants {
    public static final int WM_SETTEXT = 0x000C; // 输入文本

    public static final int WM_CHAR = 0x0102; // 输入字符

    public static final int BM_CLICK = 0xF5; // 点击事件，即按下和抬起两个动作

    public static final int KEYEVENTF_KEYUP = 0x0002; // 键盘按键抬起

    public static final int KEYEVENTF_KEYDOWN = 0x0; // 键盘按键按下

    //应用程序发送此消息来复制对应窗口的文本到缓冲区
    public static final int WM_GETTEXT = 0x0D;
    //得到与一个窗口有关的文本的长度（不包含空字符）
    public static final int WM_GETTEXTLENGTH = 0x0E;
    //鼠标左键
    public static final int VK_LBUTTON = 0x01; //Left mouse button
    //鼠标右键
    public static final int VK_RBUTTON = 0x02;//Right mouse button
    //鼠标左键点击 按下
    public static final int WM_LBUTTONDOWN = 513;
    //鼠标左键点击 抬起
    public static final int WM_LBUTTONUP = 514;

}
