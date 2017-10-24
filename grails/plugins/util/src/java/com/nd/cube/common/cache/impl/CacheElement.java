package com.nd.cube.common.cache.impl;

/**
 * cache 缓存对象封装,可以用来记录缓存过期时间
 * @author on 2015/1/14.
 */
public class CacheElement {

    //有效期,精确至毫秒
    private long expire = 0L;

    private Object value;

    /**
     * @param value
     * @param expire unix time 时间戳,单位s
     */
    public CacheElement(Object value,long expire){
        this.expire = expire * 1000L;
        this.value = value;
    }

    /**
     * 判读对象是否已过期
     * @return
     */
    public boolean isInvalidate(){
        return  System.currentTimeMillis() > expire ;
    }

    public long getExpire() {
        return expire;
    }

    public Object getValue() {
        return value;
    }
}
