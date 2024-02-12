package com.cy.toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/**
 * cmdUtil cmd命令行工具类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/08 14:20
 **/
public class CmdUtil {
    /**
     *  关闭占用指定端口的进程
     * @param port
     */
    public static void closeProcessOnPort(int port) {
        String command = "cmd /c netstat -ano | findstr :" + port;

        try {
            // 执行命令并获取输出
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // 获取占用指定端口的进程PID
                String[] parts = line.trim().split("\\s+");
                String pid = parts[parts.length - 1];
                // 关闭该进程
                killProcess(pid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  关闭指定进程
     * @param pid
     */
    public static void killProcess(String pid) {
        String command = "cmd /c taskkill /F /PID " + pid;
        try {
            // 执行命令
            Runtime.getRuntime().exec(command);
            System.out.println("已成功关闭占用指定端口的进程：" + pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断指定端口是否被占用
     * @param port
     * @return
     */
    public static boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false; // 如果端口没有被占用，返回 false
        } catch (IOException e) {
            return true; // 如果端口被占用，返回 true
        }
    }
}