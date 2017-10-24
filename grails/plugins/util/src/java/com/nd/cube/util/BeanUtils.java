package com.nd.cube.util;

import java.lang.reflect.Field;

/**
 * JavaBean相关操作
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 获取类静态变量值
     * @param clazz
     * @param propertyName
     * @return if(成员不存在){返回null}
     */
    public static Object getStaticMember(Class clazz,String propertyName) {
        //groovy  clazz["propertyName"]
        try {
            Field field = clazz.getDeclaredField(propertyName);
            if(field != null){
                field.setAccessible(true);
                return field.get(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}