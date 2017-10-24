package com.nd.grails.plugin.redis.serializer.impl

import com.nd.grails.plugin.redis.serializer.StringSerializer
import grails.converters.JSON
import groovy.json.JsonSlurper

import java.lang.reflect.Type

/**
 * 将对象序列化为字符串
 * @since 2014/7/28
 */
class JsonSlurperStringSerializer implements StringSerializer{
    @Override
    String serialize(Object obj) {
        if(obj){
           return obj as JSON
        }
        return null
    }

    @Override
    def <T> T deserialize(String str, Type cls) {
        if(str){
            def result = new JsonSlurper().parseText(str)
            if(cls instanceof List || cls instanceof Map){
                return  result
            }else {
                cls.newInstance(result)
            }
        }
        return null
    }
}
