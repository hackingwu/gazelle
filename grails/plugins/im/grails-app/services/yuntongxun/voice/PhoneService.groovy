package yuntongxun.voice

import com.cloopen.rest.sdk.CCPRestSDK
import grails.transaction.Transactional
import yuntongxun.IMProperty

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Transactional
class PhoneService {

    ScheduledExecutorService scheduledExecutorService = null;

    ArrayBlockingQueue<PhoneCall> phoneCallArrayBlockingQueue = null;

//    private PhoneCall currentPhoneCall;
    
    PhoneService() {

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        phoneCallArrayBlockingQueue = new ArrayBlockingQueue<>(1000);

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            String serverAddress = IMProperty.serverAddress
            String serverPort    = IMProperty.serverPort
            String subAccountSid    = IMProperty.subAccountSid
            String subAccountToken  = IMProperty.subToken
            String appId         = IMProperty.appId
            CCPRestSDK restAPI = new CCPRestSDK();

            @Override
            void run() {
                restAPI.init(serverAddress,serverPort);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
                restAPI.setSubAccount(subAccountSid, subAccountToken);// 初始化主帐号名称和主帐号令牌
                restAPI.setAppId(appId);// 初始化应用ID
                while (true){
                    PhoneCall currentPhoneCall = phoneCallArrayBlockingQueue.take()
                    synchronized (currentPhoneCall){
                        if (currentPhoneCall != null && currentPhoneCall.retry < 3){
                            HashMap<String, Object> result = restAPI.callback(currentPhoneCall.from,currentPhoneCall.to,currentPhoneCall.toDisplayNum,currentPhoneCall.fromDisplayNum,currentPhoneCall.promptTone,currentPhoneCall.retry+"",currentPhoneCall.maxCallTime,currentPhoneCall.hangupCdUrl);
                            if (!"000000".equals(result.get("statusCode"))){
                                log.error("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
                            }
                        }
//                        currentPhoneCall.wait();
                    }
                }
            }
        },0,5,TimeUnit.SECONDS)
    }


    def hangUp(PhoneCall currentPhoneCall){
        if (currentPhoneCall.byeType < 0) { //呼叫失败
            if (currentPhoneCall.byeType == -1 || currentPhoneCall.byeType == -2 || currentPhoneCall.byeType == -5 || currentPhoneCall.byeType == -8 || currentPhoneCall.byeType == -9) {//被叫问题
                currentPhoneCall.setRetry(++currentPhoneCall.getRetry())
                phoneCallArrayBlockingQueue.offer(currentPhoneCall)
            } else if (currentPhoneCall.byeType == -3 || currentPhoneCall.byeType == -4 || currentPhoneCall.byeType == -10) {//主叫问题
                phoneCallArrayBlockingQueue.offer(currentPhoneCall)
            } else {//鉴权问题

            }
        }
//        currentPhoneCall.notify();
    }
    
    def call(PhoneCall phoneCall) {
        phoneCallArrayBlockingQueue.put(phoneCall);
    }
}
