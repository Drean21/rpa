package com.cy.rpa.feedback;

import org.junit.Test;

import java.io.File;

/**
 * @author wjk
 * @Title:
 * @Package com.jrrl.rpa.feedback
 * @Description:
 * @date 2022/5/18 16:02
 */
public class FeedbackUtils {



    @Test
    public void test2(){
        FeedbackModel model = new FeedbackModel();
        model.setTaskId("20");
        model.setSuccess(1);
        model.setMsg("成功");
//        model.setAddress("https://///");
        String feedback = FeedbackUtils.feedback(model);
        System.out.println(feedback);
    }

    /**
     * 上传文件压缩文件
     * @param file
     * @return
     */
    public static String uploadFile(File file){
        return "";
    }



    /**
     * 反馈任务执行信息
     * @param param
     * @return
     */
    public static String feedback(FeedbackModel param){
        return "";
    }

    /**
     * 高级异常信息,当出现任务级别异常导致任务无法进行的时候调用该方法
     * 反馈任务执行信息
     * @param msg
     * @return
     */
    public static void highLevelException(String msg){
       // System.out.println("反馈参数:"+model.toString()+";响应数据:"+execute.body());
       // System.out.println("反馈参数:"+"{commitId:"+RpaApplication.taskId+",success:失败,"+"transactionNo:"+RpaApplication.transactionNo+"}"+";响应数据:"+execute.body());
        System.out.println("异常信息:"+msg);
        System.out.println("即将关闭程序....");
        //RpaApplication.shotdown();
    }
}
