package com.nd.cube.common.cache.impl

import grails.plugin.redis.RedisService

/**
 * Created by Administrator on 2015/2/3.
 */
class RedisCacheTest extends GroovyTestCase {
    RedisCache redisCache
    Long ttl = 1
    void setUp() {
        super.setUp()
        Properties properties = new Properties()
        properties.setProperty("redisService","redisService")
        properties.setProperty("ttl",ttl+"")
        RedisCacheProvider redisCacheProvider = new RedisCacheProvider()
        redisCache = redisCacheProvider.buildCache(properties)
    }
    void testGet() {
        String key = "testGet"
        String value = "testGet"
        redisCache.put(key,value)
        assertTrue(redisCache.get(key) == value)
    }

    void testPut() {
        String anotherValue = null
        new Thread(){
            public void run(){
                String key = "testPut"
                String value = "testPut"
                redisCache.put(key,value)
                Thread.currentThread().sleep(ttl*1000)
                anotherValue = redisCache.get(key)
            }
        }.start()
        assertNull(anotherValue)
    }

    void testPut1() {
        String anotherValue = null
        new Thread(){
            public void run(){
                String key = "testPut"
                String value = "testPut"
                Long expire = 2L
                redisCache.put(key,value,expire)
                Thread.currentThread().sleep(expire*1000)
                anotherValue = redisCache.get(key)
            }
        }.start()
        assertNull(anotherValue)
    }

    void testPutIfAbsent() {
        String key = "testPutInfAbsent"
        String value = null
        redisCache.putIfAbsent(key,value)
        assertNull(redisCache.get(key))
    }

    void testPutIfAbsent1() {
        String key = "testPutInfAbsent"
        String value = null
        redisCache.putIfAbsent(key,value,2)
        assertNull(redisCache.get(key))
    }

    void testContainsKey() {
        String key = "testContainsKey"
        String value = "testContainsKey"
        redisCache.put(key,value)
        assertTrue(redisCache.containsKey(key))
    }

    void testRemove() {
        String key = "testRemove"
        String value = "testRemove"
        redisCache.put(key,value)
        assertTrue(redisCache.get(key)==value)
        redisCache.remove(key)
        assertTrue(redisCache.get(key)==null)
    }

    void testInvalidate() {
        String key = "testInvalidate"
        String value = "testInvalidate"
        redisCache.put(key,value)
        assertTrue(redisCache.get(key)==value)
        redisCache.remove(key)
        assertTrue(redisCache.get(key)==null)
    }
}
