package yuntongxun.voice

import grails.test.mixin.TestFor
import im.PhoneController

/**
 * Created by Zhijian on 2015/7/8.
 */

@TestFor(PhoneController)
class PhoneControllerTest {
    void testHangupCallback(){
        request.method = "POST"
        controller.request.contentType = "text/plain"
        controller.request.content = """
            <CDR>
              <appId>ff8080813f84717a013f847540ef1111</appId>
              <callSid>14080711103332060001000200000003</callSid>
              <userData>66666666</userData>
              <subId>ff8080813f855ff9013f8568fa310001</subId>
              <caller>07175322232</caller>
              <called>18701696245</called>
              <starttime>20150327145921</starttime>
              <endtime>20150327145941</endtime>
              <duration>20</duration>
              <beginCallTime>20150327145858</beginCallTime>
              <ringingBeginTime>20150327145906</ringingBeginTime>
              <ringingEndTime>20150327145921</ringingEndTime>
              <byetype>3</byetype>
              <recordurl>http://192.168.111.23:80/downloadurl/20150327145912.wav</recordurl>
            </CDR>
        """.stripIndent().getBytes()
        controller.hangupCallback()
    }

}
