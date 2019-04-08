package com.example.myfiles.other;

import com.github.qcloudsms.*;

import java.util.Date;


public class Duanxin {
    // 短信应用SDK AppID
    public static int deleteCodeTime=1;
    int appid = 1400086565; // 1400开头

    // 短信应用SDK AppKey
    String appkey = "e36f9c11111d87bcba72c6cf5b4c1231";

    // 需要发送短信的手机号码
    //String[] phoneNumbers = {"15557116181", "17326075885","13588893896","17376506570","13511280718"};

    // 短信模板ID，需要在短信应用中申请
    int templateId = 140618 ; // NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请

    // 签名
    String smsSign = "浅时光"; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`
    public boolean sendDuanXin(String type,String code,String time,String phoneNumber){

        try {
            deleteCodeTime = Integer.parseInt(time);   //设置短信验证码存留时间
            String[] params = {type,code,time};
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.send(0, "86",phoneNumber,
                    params[0]+"验证码："+params[1]+",请于"+params[2]+"分钟内填写。如非本人操作，请忽略本短信。", "", "");
//            System.out.print(result);
            return result.result==0;
        } catch (Exception e){
            return false;
        }
    }

    public String getOnePhoneMessage(String phoneNumber,int day){
        try {
            long now = new Date().getTime()/1000;
            long beginTime = now-86400*day;  // 开始时间(UNIX timestamp)   几天前
            long endTime = now;    // 结束时间(UNIX timestamp)
            int maxNum = 10;             // 单次拉取最大量
            SmsMobileStatusPuller mspuller = new SmsMobileStatusPuller(appid, appkey);

            // 拉取短信回执
            SmsStatusPullCallbackResult callbackResult = mspuller.pullCallback("86",
                    phoneNumber, beginTime, endTime, maxNum);
//            System.out.println("回执："+callbackResult);

            // 拉取回复
            SmsStatusPullReplyResult replyResult = mspuller.pullReply("86",
                    phoneNumber, beginTime, endTime, maxNum);
//            System.out.println("回复："+replyResult);
            return replyResult.toString();
        } catch (Exception e) {
            // HTTP响应码错误
            e.printStackTrace();
            return null;
        }

    }

}
