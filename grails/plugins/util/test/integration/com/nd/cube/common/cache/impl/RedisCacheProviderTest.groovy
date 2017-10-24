package com.nd.cube.common.cache.impl

import grails.util.Holders

/**
 * Created by wuzj on 2015/2/3.
 */
class RedisCacheProviderTest extends GroovyTestCase {
    void testBuildCache() {

        Properties properties = new Properties()
        properties.setProperty("redisService","redisService")
        properties.setProperty("ttl","1")
        RedisCacheProvider redisCacheProvider = new RedisCacheProvider()
        RedisCache redisCache = redisCacheProvider.buildCache(properties)
        assertNotNull(redisCache)
    }
}
