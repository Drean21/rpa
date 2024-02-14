package com.cy.rpa.config;

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
    //环境目录
    public static final String envPath=getEnvPath();

    //临时文件目录
    public static  final String cachePath = envPath + File.separator + "cache";
    //日志文件目录
    public static  final String logPath = envPath + File.separator + "log";

    //下载文件保存路径
    public static String downloadPath = envPath + File.separator + "file";


    //cmd传参参数开头字符
    public static final String cmd_param_start = "-D";
    //cmd传参任务id参数名称
    public static final String cmd_param_workIdKey = "DtaskId";
    //cmd传参任务id参数名称
    public static final String cmd_param_commitIdKey = "DcommitId";
    //cmd传参交易号
    public static final String cmd_param_transactionNo ="DtransactionNo";
    //cmd传参参数开头字符
    public static final String cmd_param_paramKey = "Dparams";
    //数据文件地址
    public static final String cmd_param_fileUrlKey = "DfileUrl";
    //反馈地址
    public static final String cmd_param_callbackUrlKey = "DcallbackUrl";
    //    //文件上传地址
//    public static final String cmd_param_fileUploadUrlKey = "DfileUploadUrl";
    //RPA管理平台应用编号appCode
    public static final String cmd_param_appCodeKey = "DappCode";












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