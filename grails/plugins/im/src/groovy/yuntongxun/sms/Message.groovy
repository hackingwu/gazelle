package yuntongxun.sms

/**
 * Created by Zhijian on 2015/7/7.
 */
class Message {

    String to;

    String templateId;

    String[] datas;

    Message(String to, String templateId, String[] datas) {
        this.to = to
        this.templateId = templateId
        this.datas = datas
    }
}
