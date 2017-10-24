package im

import grails.converters.JSON
import groovy.json.JsonSlurper
import yuntongxun.sms.Message

class MessageController {

    def messageService

    def sendMessage() {
        Map messageMap = new JsonSlurper().parseText(request.reader.text)
        Message message = new Message(messageMap.to,messageMap.templateId,messageMap.datas.toArray(new String[messageMap.datas.size()]))
        messageService.sendMessage(message)
        render ([success:true] as JSON)
    }
}
