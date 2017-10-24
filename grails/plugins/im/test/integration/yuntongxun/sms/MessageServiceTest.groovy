package yuntongxun.sms

import yuntongxun.IMProperty


/**
 * Created by Zhijian on 2015/7/7.
 */
class MessageServiceTest extends GroovyTestCase{

    //todo: 短信发送记录，电话回拨记录是否需要存数据库

    MessageService messageService;

    void testSendMessage() {
        String to = "18506049535";
        String templateId = IMProperty.vCodeTemplateId;
        String code = "ABC"
        String[] datas = ["123456","5"];
        Random random1 = new Random();
        Random random2 = new Random();
        for(int i = 0 ; i < 1 ;i ++){
            datas[0] = 10000+i+"";
            datas[1] = (1000-i)+"";
            Message message = new Message(to,templateId,datas);
            messageService.sendMessage(message);
        }

        Thread.currentThread().sleep(10000L);
    }
}
