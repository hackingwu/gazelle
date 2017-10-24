package yuntongxun.voice

/**
 * Created by Zhijian on 2015/7/7.
 */
class PhoneCall {

    String from; //主叫电话号码
    String to;  //被叫电话号码
    String fromDisplayNum; //主叫方显示的电话号码
    String toDisplayNum; //被叫方显示的电话号码
    String promptTone;   //自定义回拨提示音
    String userData = "0";
    String maxCallTime;
    String hangupCdUrl;
    int    retry;
    int    byeType;
    PhoneCall(String from, String to) {
        this.from = from
        this.to = to
    }

    PhoneCall(String from, String to, String hangupCdUrl) {
        this.from = from
        this.to = to
        this.hangupCdUrl = hangupCdUrl
    }
}
