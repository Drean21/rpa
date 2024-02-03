package com.cy.rpa;

import com.cy.RpaApplication;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * RpaConfig rpa任务配置
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/02 15:14
 **/
public class RPAConfig implements Serializable {
    public static final String envPath=getEnvPath();


    /**
     * 获取运行环境路径
     * @return
     */
    public static String getEnvPath() {
        // 获取当前工作目录[不靠谱]
        String path = RpaApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String localUrl= new File(path).getPath();
        String envPath=getParentPath(localUrl,2);
        //解决中文路径
        try {
            envPath = java.net.URLDecoder.decode(envPath,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return envPath;

    }

    public static void main(String[] args) {
        System.out.println(envPath);
    }


    /**
     * 获取路径回退父级目录
     * @return
     */
    private static String getParentPath(String fullPath, int levelsUp) {
        Path path = Paths.get(fullPath);

        for (int i = 0; i < levelsUp; i++) {
            path = path.getParent();
            if (path == null) {
                // 如果到达根目录，提前结束循环
                break;
            }
        }

        return path != null ? path.toString() : null;
    }
}