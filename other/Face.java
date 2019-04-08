package com.example.myfiles.other;

import com.qcloud.image.ImageClient;
import com.qcloud.image.exception.AbstractImageException;
import com.qcloud.image.request.FaceDetectRequest;
import com.qcloud.image.request.FaceGetFaceInfoRequest;
import com.qcloud.image.request.FaceIdentifyRequest;

public class Face {

    String appId = "1253232442";

    String secretId = "AKIDHQO9g1H4kALHxMlO2PXP1NUKuGQKbPaV";

    String secretKey = "oNhmbATZQ2QbXEguufARUIrBIBpEOK1T";

    String bucketName = "junbaba-1253232442";

    ImageClient imageClient = new ImageClient(appId, secretId, secretKey, ImageClient.NEW_DOMAIN_recognition_image_myqcloud_com/*根据文档说明选择域名*/);
    /**

     * 人脸识别操作

     */

    public String faceFaceIdentify(String url) {

        String ret = null;

        String groupId = "groupA";

        FaceIdentifyRequest faceIdentifyReq = new FaceIdentifyRequest(bucketName, groupId, url);// 一个 groupId

        try {

            ret = imageClient.faceIdentify(faceIdentifyReq);

        } catch (AbstractImageException e) {

            e.printStackTrace();

        }
        return ret;

    }

    /**

     * 人脸检测

     */

    public String faceDetect(String url) {

        String ret = null;

        FaceDetectRequest faceDetectReq = new FaceDetectRequest(bucketName, url, 1);
        try {

            ret = imageClient.faceDetect(faceDetectReq);

        } catch (AbstractImageException e) {

            e.printStackTrace();

        }

       return ret;
    }
}
