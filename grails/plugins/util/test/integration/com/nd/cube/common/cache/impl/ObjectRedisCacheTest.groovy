package com.nd.cube.common.cache.impl

/**
 * Created by Administrator on 2015/2/3.
 */
class ObjectRedisCacheTest extends GroovyTestCase {
    ObjectRedisCache objectRedisCache
    Long ttl = 1L
    void setUp() {
        super.setUp()
        Properties properties = new Properties()
        properties.setProperty("redisService","redisService")
        properties.setProperty("ttl","1")
        RedisCacheProvider redisCacheProvider = new RedisCacheProvider()
        RedisCache redisCache = redisCacheProvider.buildCache(properties)
        objectRedisCache = new ObjectRedisCache()
        objectRedisCache.redisCache = redisCache

    }

    void testGet() {
        String key = "testGet"
        String value = "testGet"
        objectRedisCache.put(key,value)
        assertTrue(objectRedisCache.get(key) == value)
    }

    void testPut() {
        String anotherValue = null
        new Thread(){
            public void run(){
                String key = "testPut"
                String value = "testPut"
                objectRedisCache.put(key,value)
                Thread.currentThread().sleep(ttl*1000)
                anotherValue = objectRedisCache.get(key)
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
                objectRedisCache.put(key,value,expire)
                Thread.currentThread().sleep(expire*1000)
                anotherValue = objectRedisCache.get(key)
            }
        }.start()
        assertNull(anotherValue)
    }

    void testPutIfAbsent() {
        String key = "testPutInfAbsent"
        String value = null
        objectRedisCache.putIfAbsent(key,value)
        assertNull(objectRedisCache.get(key))
    }

    void testPutIfAbsent1() {
        String key = "testPutInfAbsent"
        String value = null
        objectRedisCache.putIfAbsent(key,value,2)
        assertNull(objectRedisCache.get(key))
    }

    void testContainsKey() {
        String key = "testContainsKey"
        String value = "testContainsKey"
        objectRedisCache.put(key,value)
        assertTrue(objectRedisCache.containsKey(key))
    }

    void testRemove() {
        String key = "testRemove"
        String value = "testRemove"
        objectRedisCache.put(key,value)
        assertTrue(objectRedisCache.get(key)==value)
        objectRedisCache.remove(key)
        assertTrue(objectRedisCache.get(key)==null)
    }

    void testInvalidate() {
        String key = "testInvalidate"
        String value = "testInvalidate"
        objectRedisCache.put(key,value)
        assertTrue(objectRedisCache.get(key)==value)
        objectRedisCache.remove(key)
        assertTrue(objectRedisCache.get(key)==null)
    }
}
