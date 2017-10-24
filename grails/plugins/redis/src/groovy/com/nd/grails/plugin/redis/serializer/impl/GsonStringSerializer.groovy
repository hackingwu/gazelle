package com.nd.grails.plugin.redis.serializer.impl

import com.google.gson.Gson
import com.nd.grails.plugin.redis.serializer.StringSerializer
import grails.converters.JSON

import java.lang.reflect.Type

/**
 * Created by Administrator on 2014/7/28.
 */
class GsonStringSerializer implements StringSerializer{
    @Override
    String serialize(Object obj) {
        if(obj){
//            Gson gson = new Gson()
//
//            return gson.toJson(obj)
			return (String)(obj as JSON)
        }
        return null
    }

    @Override
    def <T> T deserialize(String str,Type cls) {
        if(str){
            Gson gson = new Gson()
           return gson.fromJson(str,cls)
        }
        return null
    }
}
