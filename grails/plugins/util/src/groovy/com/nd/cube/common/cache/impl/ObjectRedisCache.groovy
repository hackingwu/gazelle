package com.nd.cube.common.cache.impl

import com.nd.cube.common.cache.Cache
import grails.converters.JSON
import groovy.json.JsonSlurper

/**
 * Created by wuzj on 15-2-1.
 */
class ObjectRedisCache implements Cache{
    int ttl = -1;
    RedisCache redisCache

    public static final String JSON_MAP_KEY = "cube"
    @Override
    Object get(Object key) {
        return unpack(redisCache.get(pack(key)))
    }

    @Override
    void put(Object key, Object value) {
        this.put(key,value,ttl)
    }

    @Override
    void put(Object key, Object value, long expire) {
        redisCache.put(pack(key),pack(value),expire)
    }

    @Override
    void putIfAbsent(Object key, Object value) {
        this.putIfAbsent(key,value,ttl)
    }

    @Override
    void putIfAbsent(Object key, Object value, long expire) {
        redisCache.putIfAbsent(pack(key),pack(value),expire)
    }

    @Override
    boolean containsKey(Object key) {
        return redisCache.containsKey(pack(key))
    }

    @Override
    void remove(Object key) {
        redisCache.remove(pack(key))
    }

    @Override
    void invalidate(Object key) {
        redisCache.invalidate(pack(key))
    }

    private String pack(Object value){
        Map m = new HashMap()
        m.put(JSON_MAP_KEY,value)
        return m as JSON
    }
    private Object unpack(String json){
        Object object = null
        try{
            if (json!=null){
                object = new JsonSlurper().parseText(json).get(JSON_MAP_KEY)
            }
        }catch (Exception e){

        }
        return object
    }
}
