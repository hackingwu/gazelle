package com.nd.cube.util;

import java.io.*;

/**
 * Created by wuzj on 2015/2/5.
 */
public class Serializer {
    public static byte[] serialize(final Serializable object){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        }catch (Exception e){

        }
        return byteArrayOutputStream.toByteArray();
    }
    public static Object deserialize(final byte[] bytes){
        if (bytes == null || bytes.length == 0){
            return null ;
        }
        Object serializable = null ;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            serializable = objectInputStream.readObject();
        }catch (Exception e){

        }
        return serializable;
    }
}
