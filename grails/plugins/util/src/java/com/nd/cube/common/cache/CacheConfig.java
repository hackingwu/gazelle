package com.nd.cube.common.cache;

import framework.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Properties;

/**
 * cache 配置
 *xufb 2015/1/30.
 */
public class CacheConfig {

    //private long ttl = ExpireTimes.FOREVER;

    public final static String providerClassName = "providerClass";
    public final static String ttlName = "ttl";

    private Properties properties = new Properties();


    /**
     * 设置配置项,配置参数
     * @param key 参数名
     * @param value 参数值
     */
    public void set(String key,Object value){
        properties.put(key,value);
    }

    /**
     * 获取配置值
     * @param key
     * @return
     */
    public Object get(String key){
        return properties.get(key);
    }

    public long getTtl(){
       // return this.ttl;
       Object ttl =  properties.get(ttlName);
        if(ttl != null){
            if(ttl instanceof Number){
                return ((Number) ttl).longValue();
            }else if(ttl instanceof String){
               if(StringUtils.isNumeric((String)ttl)){
                   return Long.valueOf((String)ttl);
               }
            }
        }
        return ExpireTimes.FOREVER;
    }

    public void setTtl(long ttl) {
        properties.put(ttlName,ttl);
    }

    public String getProviderClass() {
       Object object = properties.get(providerClassName);
        if(object != null && object instanceof Class){
            return ((Class)object).getName();
        }
        return properties.getProperty(providerClassName);
    }

    public void setProviderClass(String providerClass) {
        properties.put(providerClassName,providerClass);
    }

    public Properties toProperties(){
        return properties;
    }
}
