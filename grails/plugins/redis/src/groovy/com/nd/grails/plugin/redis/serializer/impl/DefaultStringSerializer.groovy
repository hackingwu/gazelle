package com.nd.grails.plugin.redis.serializer.impl

import com.nd.grails.plugin.redis.serializer.StringSerializer

import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * 将对象序列化为字符串
 * @since 2014/7/28
 */
class DefaultStringSerializer  implements StringSerializer{

    final Charset charset = Charset.forName('ISO-8859-1')

    @Override
    String serialize(Object obj) {

        ByteArrayOutputStream output = new ByteArrayOutputStream()

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(output)
        objectOutputStream.writeObject(obj)
        objectOutputStream.flush()

        return new String(output.toByteArray(),charset)
    }

    @Override
    def <T> T deserialize(String str, Type cls) {

        if(str){
            ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes(charset))

            ObjectInputStream ois = new ObjectInputStream(input)

            return (T)ois.readObject()
        }
        return null
    }
}
