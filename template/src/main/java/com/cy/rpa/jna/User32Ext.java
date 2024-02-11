package com.cy.rpa.jna;

import com.sun.jna.platform.win32.User32;

/**
 *User32 接口 书写与windows User32本地库同名的方法,实现本地库调用
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/09 22:05
 */

public interface User32Ext extends User32 {

    /**
     * 查找窗口
     *
     * @param lpParent     需要查找窗口的父窗口
     * @param lpChild      需要查找窗口的子窗口
     * @param lpClassName  类名
     * @param lpWindowName 窗口名
     * @return 找到的窗口的句柄
     */
    HWND FindWindowEx(HWND lpParent, HWND lpChild, String lpClassName, String lpWindowName);

    /**
     * 获取桌面窗口，可以理解为所有窗口的root
     *
     * @return 获取的窗口的句柄
     */
    HWND GetDesktopWindow();

    /**
     * 发送事件消息
     *
     * @param hWnd    控件的句柄
     * @param dwFlags 事件类型
     * @param bVk     虚拟按键码
     * @param lParam  扩展信息，传0即可
     * @return
     */
    int SendMessage(HWND hWnd, int dwFlags, int bVk, int lParam);

    /**
     * 发送事件消息
     *
     * @param hWnd    控件的句柄
     * @param dwFlags 事件类型
     * @param bVk     虚拟按键码
     * @param buffer  扩展信息，传0即可
     * @return
     */
    int SendMessage(HWND hWnd, int dwFlags, int bVk, char[] buffer);

    /**
     * 发送事件消息
     *
     * @param hWnd   控件的句柄
     * @param Msg    事件类型
     * @param wParam 传0即可
     * @param lParam 需要发送的消息，如果是点击操作传null
     * @return
     */
    int SendMessage(HWND hWnd, int Msg, int wParam, String lParam);

    /**
     * 发送键盘事件
     *
     * @param bVk         虚拟按键码
     * @param bScan       传 ((byte)0) 即可
     * @param dwFlags     键盘事件类型
     * @param dwExtraInfo 传0即可
     */
    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);

    /**
     * 激活指定窗口（将鼠标焦点定位于指定窗口）
     *
     * @param hWnd    需激活的窗口的句柄
     * @param fAltTab 是否将最小化窗口还原
     */
    void SwitchToThisWindow(HWND hWnd, boolean fAltTab);

    /**
     * 移动窗体到指定位置
     * 该函数改变指定窗口的位置和尺寸。对于顶层窗口，位置和尺寸是相对于屏幕的左上角的：对于子窗口，位置和尺寸是相对于父窗口客户区的左上角坐标的。
     *
     * @param hWnd    窗体句柄
     * @param x       需要移动到的x位置
     * @param y       需要移动到的y位置
     * @param width   窗体的宽度
     * @param height  窗体的高度
     * @param repaint 默认为true
     * @return
     */
    boolean MoveWindow(HWND hWnd, int x, int y, int width, int height, boolean repaint);
}
