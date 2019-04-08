package com.example.myfiles.other;

import com.qcloud.image.ImageClient;
import com.qcloud.image.exception.AbstractImageException;
import com.qcloud.image.request.GeneralOcrRequest;

public class Ocr {

    String appId = "1253232442";

    String secretId = "AKIDHQO9g1H4kALHxMlO2PXP1NUKuGQKbPaV";

    String secretKey = "oNhmbATZQ2QbXEguufARUIrBIBpEOK1T";

    String bucketName = "junbaba-1253232442";

    ImageClient imageClient = new ImageClient(appId, secretId, secretKey, ImageClient.NEW_DOMAIN_recognition_image_myqcloud_com/*根据文档说明选择域名*/);

    public String getOcrGeneral(String url){
//        url = "http://junbaba-1253232442.cos.ap-shanghai.myqcloud.com/%E5%A4%A7%E4%B8%89%E4%B8%8A%E5%AD%A6%E6%9C%9F%E8%AF%BE%E8%A1%A8.jpg?sign=q-sign-algorithm%3Dsha1%26q-ak%3DAKIDHQO9g1H4kALHxMlO2PXP1NUKuGQKbPaV%26q-sign-time%3D1541070835%3B1541073835%26q-key-time%3D1541070835%3B1541073835%26q-header-list%3Dhost%26q-url-param-list%3D%26q-signature%3D78aa36c1e8d031a538d6c1da7454f7524333d547";
        String ret = null;
        GeneralOcrRequest request = new GeneralOcrRequest(bucketName, url);
        try {
            ret = imageClient.generalOcr(request);
        } catch (AbstractImageException e) {
            e.printStackTrace();

        }
        return ret;
    }

}
