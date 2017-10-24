package com.nd.grails.plugin.redis.serializer

import java.lang.reflect.Type

/**
 * Created by Administrator on 2014/7/28.
 */
public interface StringSerializer {

    String serialize(Object obj)


    public <T>T deserialize(String str,Type cls)

}