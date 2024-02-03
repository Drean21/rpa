package com.cy;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSONObject;
import com.cy.rpa.JobWorker;
import com.cy.rpa.annotations.Job;
import com.cy.rpa.annotations.Jobs;
import com.cy.rpa.feedback.FeedbackModel;
import com.cy.rpa.feedback.FeedbackUtils;
import lombok.Data;
import org.junit.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Objects;

@SpringBootApplication
public class RpaApplication {
    //开发:false;  生产:true;
    public static boolean model = false;
    //默认的程序启动端口
    public static String defaultPort = "8080";
    /*任务id*/
    public static String taskId = "";

    public static String transactionNo;

    public static String appCode;

    /*集合或者增减员数据地址*/
    public static String dataUrl = "";
    /*反馈接口*/
    public static String callbackUrl = "";
//    /*文件上传接口*/
//    public static String fileUploadUrl = "";


    public static void main(String[] args) {
        // 打开文件扩展名
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("reg", "add", "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Advanced", "/v", "HideFileExt", "/t", "REG_DWORD", "/d", "0", "/f");
            processBuilder.start();
        } catch (IOException e) {
            System.out.println("打开文件扩展名失败：" + e);
        }
        SpringApplicationBuilder builder = new SpringApplicationBuilder(RpaApplication.class);
        builder.headless(false).web(WebApplicationType.NONE).properties(Collections
                .singletonMap("server.port", defaultPort)).run(args);

        CMDParam cmdParam = getCMDParam(args);
        if (!Objects.isNull(cmdParam)) {
            System.out.println("获取到参数:" + cmdParam.toString());
            taskId = cmdParam.getWorkId();
            String paramBase64Str = cmdParam.getParam();
            dataUrl = cmdParam.getFileUrl();
            try {
                JSONObject param = JSONObject.parseObject(Base64.decodeStr(paramBase64Str));
                //run(param, dataUrl, appCode);
            } catch (Exception e) {
                e.printStackTrace();
                // 异常则反馈失败给管理平台
                FeedbackModel model = new FeedbackModel();
                model.setTaskId(RpaApplication.taskId);
                model.setSuccess(0);
                model.setMsg("程序发生异常，任务执行失败,原因：" + e.getMessage());
                model.setTransactionNo(Long.valueOf(RpaApplication.transactionNo));
                String feedback = FeedbackUtils.feedback(model);
                System.out.println("反馈管理平台： " + feedback);
            }
        } else {
            // todo 完善反馈处理
            System.out.println("没有传递参数过来");
        }

        //shotdown();
    }

    private static void run(String[] args) {
//        TianjinWorker worker = new TianjinWorker();
//        worker.worker();
//        WuxiMedicalInsuranceVoucherDownloadJob worker=new WuxiMedicalInsuranceVoucherDownloadJob();
//        worker.worker();


    }


    /**
     * 解析cmd传递的参数,实现启动参数配置
     *
     * @param args
     * @return
     */
    private static CMDParam getCMDParam(String[] args) {
        CMDParam param = new CMDParam();
        return param;
    }

    /**
     * 解析获取参数数据
     *
     * @param arg
     * @return
     */
    private static String getParamData(String arg) {
        String param = arg.substring(arg.indexOf("=") + 1);
        return param;
    }


    /**
     * CMD参数解析工具
     */
    @Data
    static class CMDParam {
        //工作的唯一id
        private String workId;
        //业务执行参数
        private String param;
        //业务执行参数
        private String fileUrl;
    }


    /**
     * 调度式任务类创建入口
     *
     * @param conf rpa自动化配置 网址 账号 密码 牌照 业务名称
     */
    private static void run(JSONObject conf, String dataUrl, String appCode) throws Exception {
        JobWorker<?> worker = getWork(appCode);
        if (worker != null) {
            String className = worker.getClass().getName();
            System.out.println("worker全类名:" + className);
            worker.worker(conf, dataUrl);
        } else {
            throw new Exception("work初始化失败:找不到对应appCode的workJob");
        }
    }

    @Test
    public void testRun() throws Exception {
        JobWorker<?> worker = getWork("sb_jc_zhongshan_zengyuan");
        JSONObject config = new JSONObject();
        config.put("网址", "https://etax.guangdong.chinatax.gov.cn/xxmh/");
        config.put("账号", "13790538620");
        config.put("密码", "******");
        config.put("业务名", "嘉兴医保批量增员");
        config.put("牌照", "恒英人力资源服务（宁波）有限公司嘉善分公司");
        config.put("城市", "嘉兴市");
        if (worker != null) {
            String className = worker.getClass().getName();
            System.out.println("worker全类名：:" + className);
            //worker.worker(config, "");
        } else {
            throw new Exception("work初始化失败:找不到对应appCode的workJob");
        }
    }

    /**
     * 获取rpa应用实例对象
     *
     * @param appCode rpa应用标识
     * @return
     */
    private static JobWorker<?> getWork(String appCode) throws Exception {
        Class<? extends Annotation> containerAnnotation = Jobs.class;
        Class<? extends Annotation> elementAnnotation = Job.class;
        String packageName = "com.jrrl.workerjob";

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(containerAnnotation));
        scanner.addIncludeFilter(new AnnotationTypeFilter(elementAnnotation));

        for (org.springframework.beans.factory.config.BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());

                // 检查类上的 @Jobs 注解
                Annotation containerAnn = clazz.getAnnotation(containerAnnotation);
                if (containerAnn != null) {
                    Job[] jobAnnotations = (Job[]) getValueFromAnnotation(containerAnn, "value");

                    for (Job ann : jobAnnotations) {
                        if (ann.appCode().equals(appCode)) {
                            Constructor<?> constructor = clazz.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            return (JobWorker<?>) constructor.newInstance();
                        }
                    }
                }

                // 检查类上的单个 @Job 注解
                Annotation elementAnn = clazz.getAnnotation(elementAnnotation);
                if (elementAnn != null) {
                    Job jobAnnotation = (Job) elementAnn;
                    if (jobAnnotation.appCode().equals(appCode)) {
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return (JobWorker<?>) constructor.newInstance();
                    }
                }
            } catch (Exception e) {
                throw new Exception("work初始化失败" + e.getMessage());
            }
        }

        return null;
    }

    /**
     * 获取注解对象特定属性的值
     *
     * @param annotation
     * @param fieldName
     * @return
     */
    private static Object getValueFromAnnotation(Annotation annotation, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return annotation.annotationType().getMethod(fieldName).invoke(annotation);
    }


    /**
     * 关闭执行程序
     */
    public static void shotdown() {
        System.exit(0);
    }
}
