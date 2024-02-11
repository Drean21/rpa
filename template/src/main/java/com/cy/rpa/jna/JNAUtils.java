package com.cy.rpa.jna;

import cn.hutool.core.date.DateUtil;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.W32APIOptions;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * JNAUtils JNA库，提供win系统功能支持和交互
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/09 22:05
 **/
public class JNAUtils {
    //默认的文本长度
    private static final int TEXT_CHAR_ARRAY_LENGTH = 512;

    private static User32Ext USER32EXT = (User32Ext) Native.loadLibrary("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * 从桌面开始查找指定类名的组件，在超时的时间范围内，如果未找到任何匹配的组件则反复查找
     *
     * @param className 组件的类名
     * @param caption   排布的第几个组件
     * @return 返回匹配的组件的句柄，如果匹配的组件大于一个，返回第一个查找的到的；如果未找到或超时则返回<code>null</code>
     */
    public static WinDef.HWND findHandleByClassNameAndCaption(String className, String caption) {
        return findHandleByClassNameAndCaption(className, caption, 0);
    }

    /**
     * 从桌面开始查找指定类名的组件，在超时的时间范围内，如果未找到任何匹配的组件则反复查找
     *
     * @param className 组件的类名
     * @param caption   排布的第几个组件
     * @return 返回匹配的组件的句柄，如果匹配的组件大于一个，返回第一个查找的到的；如果未找到或超时则返回<code>null</code>
     */
    public static WinDef.HWND findHandleByClassNameAndCaption(String className, String caption, int index) {
        List<WinDef.HWND> hwnds = findHandlesByClassName(USER32EXT.GetDesktopWindow(), className, 1, TimeUnit.SECONDS, caption);
        if (hwnds.size() == 0) {
            return null;
        }
        try {
            if (hwnds.size() > index)
                return hwnds.get(index);
            else
                return hwnds.get(hwnds.size() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从桌面开始查找指定类名的组件，在超时的时间范围内，如果未找到任何匹配的组件则反复查找
     *
     * @param className 组件的类名
     * @param timeout   超时时间
     * @param unit      超时时间的单位
     * @param caption   排布的第几个组件
     * @return 返回匹配的组件的句柄，如果匹配的组件大于一个，返回第一个查找的到的；如果未找到或超时则返回<code>null</code>
     */
    public static WinDef.HWND findHandleByClassName(String className, long timeout, TimeUnit unit, String caption) {
        List<WinDef.HWND> hwnds = findHandlesByClassName(USER32EXT.GetDesktopWindow(), className, timeout, unit, caption);
        try {
            if (hwnds.size() > 0)
                return hwnds.get(0);
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据指定的caption从指定的窗口中获取组件
     *
     * @param root      顶层句柄
     * @param className 文件名
     * @param timeout   超时时间
     * @param unit      时间类型/单位
     * @param caption   句柄标题
     * @return
     */
    public static WinDef.HWND findHandleByClassName(WinDef.HWND root, String className, long timeout, TimeUnit unit, String caption) {
        List<WinDef.HWND> hwnds = findHandlesByClassName(root, className, timeout, unit, caption);
        if (hwnds.size() > 0)
            return hwnds.get(0);
        else
            return null;
    }


    /**
     * 从指定窗口中根据className获取第no个组件
     *
     * @param className 类名
     * @param timeout   超时时间
     * @param unit      时间类型
     * @param no        指定序号位置的元素
     * @return
     */
    public static WinDef.HWND findHandleByClassName(String className, long timeout, TimeUnit unit, int no) {
        List<WinDef.HWND> hwnds = findHandlesByClassName(USER32EXT.GetDesktopWindow(), className, timeout, unit, null);
        if (hwnds.size() >= no)
            return hwnds.get(no - 1);
        else
            return null;
    }


    /**
     * 从桌面开始查找指定类名的组件
     *
     * @param className 组件的类名
     * @return 返回匹配的组件的句柄，如果匹配的组件大于一个，返回第一个查找的到的；如果未找到任何匹配则返回<code>null</code>
     */
    public static WinDef.HWND findHandleByClassName(String className) {
        List<WinDef.HWND> list = findHandleByClassName(USER32EXT.GetDesktopWindow(), className, null);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 从桌面开始查找指定类名的组件
     *
     * @param className 组件的类名
     * @param index
     * @return 返回匹配的组件的句柄，如果匹配的组件大于一个，返回第一个查找的到的；如果未找到任何匹配则返回<code>null</code>
     */
    public static WinDef.HWND findHandleByClassName(String className, int index) {
        try {
            List<WinDef.HWND> list = findHandleByClassName(USER32EXT.GetDesktopWindow(), className, null);
            return list.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从指定位置开始查找指定类名的组件
     *
     * @param root      查找组件的起始位置的组件的句柄，如果为<code>null</code>则从桌面开始查找
     * @param className 组件的类名
     * @param timeout   超时时间
     * @param unit      超时时间的单位
     * @param caption   组件的显示内容
     * @return 返回匹配的组件的句柄，如果未找到或超时则返回<code>null</code>
     */
    private static List<WinDef.HWND> findHandlesByClassName(WinDef.HWND root, String className, long timeout, TimeUnit unit, String caption) {
        if (null == className || className.length() <= 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        List<WinDef.HWND> hwnds = findHandleByClassName(root, className, caption);
        while (hwnds.size() == 0 && (System.currentTimeMillis() - start < unit.toMillis(timeout))) {
            hwnds = findHandleByClassName(root, className, caption);
        }
        System.out.println("*** find className:" + className + " caption:" + caption + " size:" + hwnds.size() + " ***");
        return hwnds;
    }

    /**
     * 从指定位置开始查找指定类名的组件
     *
     * @param root      查找组件的起始位置的组件的句柄，如果为<code>null</code>则从桌面开始查找
     * @param className 组件的类名
     * @param caption   标题
     * @return 返回匹配的组件的句柄，如果匹配的组件大于一个，返回第一个查找的到的；如果未找到任何匹配则返回<code>null</code>
     */
    public static List<WinDef.HWND> findHandleByClassName(WinDef.HWND root, String className, String caption) {
        if (null == className || className.length() <= 0) {
            return null;
        }
        List<WinDef.HWND> result = new ArrayList();
        findHandle(result, root, className, caption);
        return result;
    }


    /**
     * 根据className+caption获取句柄，如果caption为空则不验证caption
     *
     * @param target    句柄集合
     * @param root      顶层句柄
     * @param className 句柄类名
     * @param caption   句柄标题
     * @return
     */
    private static boolean findHandle(final List<WinDef.HWND> target, WinDef.HWND root, final String className, final String caption) {
        if (null == root) {
            root = USER32EXT.GetDesktopWindow();
        }

        return USER32EXT.EnumChildWindows(root, new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
                char[] winClass = new char[TEXT_CHAR_ARRAY_LENGTH];
                USER32EXT.GetClassName(hwnd, winClass, TEXT_CHAR_ARRAY_LENGTH);

                char[] captionName = new char[TEXT_CHAR_ARRAY_LENGTH];
                if (USER32EXT.IsWindowVisible(hwnd) && className.equals(Native.toString(winClass))) {
                    //如果caption不为空，则根据caption检查
                    if (caption != null && !caption.trim().equals("")) {
                        USER32EXT.GetWindowText(hwnd, captionName, TEXT_CHAR_ARRAY_LENGTH);
                        if (Native.toString(captionName).contains(caption))
                            target.add(hwnd);
                    } else {
                        target.add(hwnd);
                    }
                } else {
                    //return target.size() == 0 || findHandle(target, hwnd, className,caption);
                }
                return true;
            }

        }, Pointer.NULL);
    }

    /**
     * 模拟键盘按键事件，异步事件。使用win32 keybd_event，每次发送KEYEVENTF_KEYDOWN、KEYEVENTF_KEYUP两个事件。默认10秒超时
     *
     * @param hwnd           被键盘操作的组件句柄
     * @param keyCombination 键盘的虚拟按键码（<a href="http://msdn.microsoft.com/ZH-CN/library/windows/desktop/dd375731.aspx">Virtual-Key Code</a>），或者使用{@link java.awt.event.KeyEvent}</br>
     *                       二维数组第一维中的一个元素为一次按键操作，包含组合操作，第二维中的一个元素为一个按键事件，即一个虚拟按键码
     * @return 键盘按键事件放入windows消息队列成功返回<code>true</code>，键盘按键事件放入windows消息队列失败或超时返回<code>false</code>
     */
    public static boolean simulateKeyboardEvent(WinDef.HWND hwnd, int[][] keyCombination) {
        if (null == hwnd) {
            return false;
        }
        USER32EXT.SwitchToThisWindow(hwnd, true);
        USER32EXT.SetFocus(hwnd);
        for (int[] keys : keyCombination) {
            for (int i = 0; i < keys.length; i++) {
                USER32EXT.keybd_event((byte) keys[i], (byte) 0, Win32MessageConstants.KEYEVENTF_KEYDOWN, 0); // key down
            }
            for (int i = keys.length - 1; i >= 0; i--) {
                USER32EXT.keybd_event((byte) keys[i], (byte) 0, Win32MessageConstants.KEYEVENTF_KEYUP, 0); // key up
            }
        }
        return true;
    }

    /**
     * 模拟字符输入，同步事件。使用win32 SendMessage API发送WM_CHAR事件。默认10秒超时
     *
     * @param hwnd    被输入字符的组件的句柄
     * @param content 输入的内容。字符串会被转换成<code>char[]</code>后逐个字符输入
     * @return 字符输入事件发送成功返回<code>true</code>，字符输入事件发送失败或超时返回<code>false</code>
     */
    public static boolean simulateCharInput(final WinDef.HWND hwnd, final String content) {
        if (null == hwnd) {
            return false;
        }
        try {
            return execute(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    USER32EXT.SwitchToThisWindow(hwnd, true);
                    USER32EXT.SetFocus(hwnd);
                    for (char c : content.toCharArray()) {
                        Thread.sleep(5);
                        USER32EXT.SendMessage(hwnd, Win32MessageConstants.WM_CHAR, (byte) c, 0);
                    }
                    return true;
                }

            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 模拟文本输入,字节类型输入，同步事件。使用win32 SendMessage API发送WM_SETTEXT事件。默认10秒超时
     *
     * @param hwnd                    被输入文本的组件的句柄
     * @param content                 输入的文本内容
     * @param sleepMillisPreCharInput 两个字符之间间隔时长
     * @return 文本输入事件发送成功返回<code>true</code>，文本输入事件发送失败或超时返回<code>false</code>
     */
    public static boolean simulateCharInput(final WinDef.HWND hwnd, final String content, final long sleepMillisPreCharInput) {
        if (null == hwnd) {
            return false;
        }
        try {
            return execute(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    USER32EXT.SwitchToThisWindow(hwnd, true);
                    USER32EXT.SetFocus(hwnd);
                    for (char c : content.toCharArray()) {
                        Thread.sleep(sleepMillisPreCharInput);
                        USER32EXT.SendMessage(hwnd, Win32MessageConstants.WM_CHAR, (byte) c, 0);
                    }
                    return true;
                }

            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 模拟文本输入，同步事件。使用win32 SendMessage API发送WM_SETTEXT事件。默认10秒超时
     *
     * @param hwnd    被输入文本的组件的句柄
     * @param content 输入的文本内容
     * @return 文本输入事件发送成功返回<code>true</code>，文本输入事件发送失败或超时返回<code>false</code>
     */
    public static boolean simulateTextInput(final WinDef.HWND hwnd, final String content) {
        if (null == hwnd) {
            return false;
        }
        try {
            return execute(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    USER32EXT.SwitchToThisWindow(hwnd, true);
                    USER32EXT.SetFocus(hwnd);
                    USER32EXT.SendMessage(hwnd, Win32MessageConstants.WM_SETTEXT, 0, content);
                    return true;
                }

            });
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 模拟文本输入，同步事件。使用win32 SendMessage API发送WM_SETTEXT事件。默认10秒超时
     *
     * @param hwnd    被输入文本的组件的句柄
     * @param content 输入的文本内容
     * @return 文本输入事件发送成功返回<code>true</code>，文本输入事件发送失败或超时返回<code>false</code>
     */
    public static boolean simulateTextInputs(final WinDef.HWND hwnd, final String content) {
        if (null == hwnd) {
            return false;
        }
        try {
            return execute(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    USER32EXT.SwitchToThisWindow(hwnd, true);
                    USER32EXT.SetFocus(hwnd);
                    new RobotInput().inputKeys(content);
                    return true;
                }

            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 模拟鼠标点击，同步事件。使用win32 SendMessage API发送BM_CLICK事件。默认10秒超时
     *
     * @param hwnd 被点击的组件的句柄
     * @return 点击事件发送成功返回<code>true</code>，点击事件发送失败或超时返回<code>false</code>
     */
    public static boolean simulateClick(final WinDef.HWND hwnd) {
        if (null == hwnd) {
            return false;
        }
        try {
            return execute(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    USER32EXT.SwitchToThisWindow(hwnd, true);
                    USER32EXT.SendMessage(hwnd, Win32MessageConstants.BM_CLICK, 0, (String) null);
                    return true;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取句柄的文本(标题)(内容)
     *
     * @param hwnd 句柄
     */
    public static String getWinHandleText(final WinDef.HWND hwnd) {
        if (null == hwnd) {
            return "";
        }
        char[] text = new char[TEXT_CHAR_ARRAY_LENGTH];
        try {
            return execute(new Callable<String>() {
                @Override
                public String call() {
                    USER32EXT.SendMessage(hwnd, Win32MessageConstants.WM_GETTEXT, TEXT_CHAR_ARRAY_LENGTH, text);
                    return Native.toString(text);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 使用线程池实现同步任务
     *
     * @param callable
     * @param <T>
     * @return
     * @throws Exception
     */
    private static <T> T execute(Callable<T> callable) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<T> task = executor.submit(callable);
            return task.get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdown();
        }
    }


    /**
     * 通过组件窗体类名和标题实现获取窗体
     * (顶层元素获取,非元素封装句柄,win原生或者进程顶层句柄(比如浏览器另存为确认框,程序住窗体等))
     *
     * @param className 句柄类名
     * @param title     句柄标题
     * @return
     */
    public static WinDef.HWND findHandleByClassNameAndTitle(String className, String title) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(className, title); // 第一个参数是Windows窗体的窗体类，第二个参数是窗体的标题。不熟悉windows编程的需要先找一些Windows窗体数据结构的知识来看看，还有windows消息循环处理，其他的东西不用看太多。
        if (hwnd == null) {
            System.out.println(DateUtil.now() + " not found handle,classname:" + className + ";title:" + title);
        } else {
            System.out.println(DateUtil.now() + " found handle,classname:" + className + ";title:" + title);
        }
        return hwnd;
    }


    /**
     * 获取句柄的桌面位置
     *
     * @param hwnd
     * @return
     */
    public static WinDef.RECT getWinElementRect(WinDef.HWND hwnd) {
        try {
            WinDef.RECT rect = new WinDef.RECT();
            USER32EXT.GetWindowRect(hwnd, rect);
            return rect;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据类名关闭window窗体
     *
     * @param className 窗体类名
     * @return
     */
    public static boolean closeHandleByClassName(String className) {
        WinDef.HWND hwnd = findHandleByClassName(className);
        // you need to modify this
        // for your need
        if (hwnd == null) {
            System.out.println(DateUtil.now() + " found handle,classname:" + className + ";");
            return false;
        } else {
            User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null);  // can be WM_QUIT in some occasion
            return true;
        }
    }

    /**
     * 根据类名关闭window窗体
     *
     * @param hwnd 句柄对象
     * @return
     */
    public static boolean closeHandle(WinDef.HWND hwnd) {
        try {
            User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null);  // can be WM_QUIT in some occasion
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 点击元素
     *
     * @param className
     * @param caption
     * @return
     */
    public static boolean clickHandleByClsStrAndTitle(String className, String caption) {
        WinDef.HWND handle = JNAUtils.findHandleByClassName(className, 1, TimeUnit.SECONDS, caption);
        if (handle != null) {
            return JNAUtils.simulateClick(handle);
        }
        return false;
    }

    /**
     * 判断元素是否可见 可理解为是否已经加载进入window句柄集合,在没有刷新应用的情况下.即便切换到其他的页面,返回值依然为ture
     *
     * @param hwnd 句柄对象
     * @return 元素存在, 则为true, 不存在则为false
     */
    public static boolean isVisible(WinDef.HWND hwnd) {
        if (hwnd == null) {
            return false;
        }
        if (USER32EXT.IsWindowVisible(hwnd)) {
            return true;
        }
        return false;
    }


    /**
     * 键盘输入工具类,不支持中文以及中文输入法状态下的符号
     */
    static class RobotInput {

        private  List<String> LETTER_NUMBER_LIST=null;
        private  List<String> BIG_SYMBOL=null;
        private  List<String> SMALL_SYMBOL=null;
        private Robot robot=null;

        RobotInput(){
            try {
                robot=new Robot();
                LETTER_NUMBER_LIST=new ArrayList<>();
                BIG_SYMBOL=new ArrayList<>();
                SMALL_SYMBOL=new ArrayList<>();
                Collections.addAll(LETTER_NUMBER_LIST,
                        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                        "0","1","2","3","4","5","6","7","8","9",
                        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
                Collections.addAll(BIG_SYMBOL,"~","!","@","#","$","%","^","&","*","(",")","_","+","{","}","|",":","\"","<",">","?");
                Collections.addAll(SMALL_SYMBOL,"`","-","=","[","]","\\",";","\'",",",".","/");
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        public void inputKeys(String src){
            int length=src.length();
            for(int i=0;i<length;i++){
                char c = src.charAt(i);
                String s=String.valueOf(c);
                if(LETTER_NUMBER_LIST.contains(s)){
                    writeString(c);
                }
                if(BIG_SYMBOL.contains(s)){
                    big_symbol_writeString(s);
                }
                if(SMALL_SYMBOL.contains(s)){
                    small_symbol_writestring(s);
                }
            }
        }

        //字母数字输入
        private  void writeString(char c) {
            if (Character.isUpperCase(c)) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }
            robot.keyPress(Character.toUpperCase(c));
            robot.keyRelease(Character.toUpperCase(c));
            if (Character.isUpperCase(c)) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
            robot.delay(100);
        }

        //大写符号输入
        private void big_symbol_writeString(String s) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            if(s.equals("~")) robot.keyPress(KeyEvent.VK_BACK_QUOTE);robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
            if(s.equals("!")) robot.keyPress(KeyEvent.VK_1);robot.keyRelease(KeyEvent.VK_1);
            if(s.equals("@")) robot.keyPress(KeyEvent.VK_2);robot.keyRelease(KeyEvent.VK_2);
            if(s.equals("#")) robot.keyPress(KeyEvent.VK_3);robot.keyRelease(KeyEvent.VK_3);
            if(s.equals("$")) robot.keyPress(KeyEvent.VK_4);robot.keyRelease(KeyEvent.VK_4);
            if(s.equals("%")) robot.keyPress(KeyEvent.VK_5);robot.keyRelease(KeyEvent.VK_5);
            if(s.equals("^")) robot.keyPress(KeyEvent.VK_6);robot.keyRelease(KeyEvent.VK_6);
            if(s.equals("&")) robot.keyPress(KeyEvent.VK_7);robot.keyRelease(KeyEvent.VK_7);
            if(s.equals("*")) robot.keyPress(KeyEvent.VK_8);robot.keyRelease(KeyEvent.VK_8);
            if(s.equals("(")) robot.keyPress(KeyEvent.VK_9);robot.keyRelease(KeyEvent.VK_9);
            if(s.equals(")")) robot.keyPress(KeyEvent.VK_0);robot.keyRelease(KeyEvent.VK_0);
            if(s.equals("_")) robot.keyPress(KeyEvent.VK_MINUS);robot.keyRelease(KeyEvent.VK_MINUS);
            if(s.equals("+")) robot.keyPress(KeyEvent.VK_EQUALS);robot.keyRelease(KeyEvent.VK_EQUALS);
            if(s.equals("{")) robot.keyPress(KeyEvent.VK_OPEN_BRACKET);robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
            if(s.equals("}")) robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
            if(s.equals("|")) robot.keyPress(KeyEvent.VK_BACK_SLASH);robot.keyRelease(KeyEvent.VK_BACK_SLASH);
            if(s.equals(":")) robot.keyPress(KeyEvent.VK_SEMICOLON);robot.keyRelease(KeyEvent.VK_SEMICOLON);
            if(s.equals("\"")) robot.keyPress(KeyEvent.VK_QUOTE);robot.keyRelease(KeyEvent.VK_QUOTE);
            if(s.equals("<")) robot.keyPress(KeyEvent.VK_COMMA);robot.keyRelease(KeyEvent.VK_COMMA);
            if(s.equals(">")) robot.keyPress(KeyEvent.VK_PERIOD);robot.keyRelease(KeyEvent.VK_PERIOD);
            if(s.equals("?")) robot.keyPress(KeyEvent.VK_SLASH);robot.keyRelease(KeyEvent.VK_SLASH);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.delay(100);
        }


        //小写符号输入
        private void small_symbol_writestring(String s) {
            if(s.equals("`")) robot.keyPress(KeyEvent.VK_BACK_QUOTE);robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
            if(s.equals("-")) robot.keyPress(KeyEvent.VK_MINUS);robot.keyRelease(KeyEvent.VK_MINUS);
            if(s.equals("=")) robot.keyPress(KeyEvent.VK_EQUALS);robot.keyRelease(KeyEvent.VK_EQUALS);
            if(s.equals("[")) robot.keyPress(KeyEvent.VK_OPEN_BRACKET);robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
            if(s.equals("]")) robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
            if(s.equals("\\")) robot.keyPress(KeyEvent.VK_BACK_SLASH);robot.keyRelease(KeyEvent.VK_BACK_SLASH);
            if(s.equals(";")) robot.keyPress(KeyEvent.VK_SEMICOLON);robot.keyRelease(KeyEvent.VK_SEMICOLON);
            if(s.equals("'")) robot.keyPress(KeyEvent.VK_QUOTE);robot.keyRelease(KeyEvent.VK_QUOTE);
            if(s.equals(",")) robot.keyPress(KeyEvent.VK_COMMA);robot.keyRelease(KeyEvent.VK_COMMA);
            if(s.equals(".")) robot.keyPress(KeyEvent.VK_PERIOD);robot.keyRelease(KeyEvent.VK_PERIOD);
            if(s.equals("/")) robot.keyPress(KeyEvent.VK_SLASH);robot.keyRelease(KeyEvent.VK_SLASH);
            robot.delay(100);
        }
    }
}
