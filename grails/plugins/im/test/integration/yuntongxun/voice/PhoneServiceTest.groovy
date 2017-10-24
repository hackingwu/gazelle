package yuntongxun.voice

import java.util.concurrent.TimeUnit

/**
 * Created by Zhijian on 2015/7/7.
 */
class PhoneServiceTest extends GroovyTestCase{

    PhoneService phoneService;

    void testCall() {
        PhoneCall phoneCall = new PhoneCall("18506049535","18965907997");
        phoneService.call(phoneCall);
        TimeUnit.SECONDS.sleep(5L);
    }
}
