package yuntongxun.sms

import com.cloopen.rest.sdk.CCPRestSDK
import grails.transaction.Transactional
import yuntongxun.IMProperty

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Transactional
class MessageService {

    ExecutorService executorService = null;

    ArrayBlockingQueue<Message> messageArrayBlockingQueue = null;

    MessageService() {

        executorService = Executors.newSingleThreadExecutor();

        messageArrayBlockingQueue = new ArrayBlockingQueue<>(1000);

        executorService.execute(new Runnable() {
            String serverAddress = IMProperty.serverAddress
            String serverPort    = IMProperty.serverPort
            String accountSid    = IMProperty.accountSid
            String accountToken  = IMProperty.accountToken
            String appId         = IMProperty.appId
            CCPRestSDK restAPI = new CCPRestSDK();

            @Override
            void run() {
                restAPI.init(serverAddress,serverPort);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
                restAPI.setAccount(accountSid, accountToken);// 初始化主帐号名称和主帐号令牌
                restAPI.setAppId(appId);// 初始化应用ID
                while (true){
                    Message message = messageArrayBlockingQueue.take()
                    if (message != null){
                        HashMap<String, Object> result = restAPI.sendTemplateSMS(message.to,message.templateId,message.datas)
                        if (!"000000".equals(result.get("statusCode"))){
                            log.error("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
                        }
                    }
                }
            }
        })
    }

    def sendMessage(Message message) {

        messageArrayBlockingQueue.put(message);

    }



}
