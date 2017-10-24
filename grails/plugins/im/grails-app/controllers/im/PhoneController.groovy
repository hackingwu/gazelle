package im

import grails.converters.JSON
import yuntongxun.voice.PhoneCall
import yuntongxun.voice.PhoneService

class PhoneController {

    PhoneService phoneService

    def hangupCallback() {
        int byetype = Integer.valueOf(request.getXML().getAt("byetype").text())
        int retry   = Integer.valueOf(request.getXML().getAt("userData").text())
        log.info("huib"+",byetype:"+byetype+",retry:"+retry)
        String from = request.getXML().getAt("caller").text()
        String to   = request.getXML().getAt("called").text()
        String hangupUrl = "http://120.55.125.104:8090" +"/phone/hangupCallback"
        PhoneCall phoneCall = new PhoneCall(from,to,hangupUrl)
        phoneCall.setRetry(retry)
        phoneCall.setByeType(byetype)
        phoneService.hangUp(phoneCall)
        String responseStr = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Response>
                <statuscode>000000</statuscode>
            </Response>
        """
        response.getOutputStream().write(responseStr.bytes)
    }

    def call(){
        String from = params.from
        String to = params.to
        String hangupUrl = params.hangupUrl
        PhoneCall phoneCall = new PhoneCall(from,to,hangupUrl)
        phoneService.call(phoneCall)
        render ([success:true] as JSON)
    }
}
