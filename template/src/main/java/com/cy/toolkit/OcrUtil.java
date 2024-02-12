package com.cy.toolkit;

import com.cy.rpa.config.SikulixManage;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * OcrUtil OCR工具类
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/12 12:58
 **/
public class OcrUtil {
    /**
     *  腾讯OCR识别
     * @param filePath
     * @return
     */
    public static String ocrByTencent(String filePath) {
        return "";
    }

    public static String ocrByTesseract(String filePath) {
        String text = null;
        try {
            text = SikulixManage.getTextRecognizer().recognize(ImageIO.read(new File(filePath)));
        } catch (Exception e) {
            throw new RuntimeException("OCR引擎识别出错", e);
        }
        return text;
     }
}