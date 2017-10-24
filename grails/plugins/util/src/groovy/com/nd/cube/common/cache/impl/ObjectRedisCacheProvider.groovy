package com.nd.cube.common.cache.impl

import com.nd.cube.common.cache.Cache
import com.nd.cube.common.cache.CacheBuilder
import com.nd.cube.common.cache.CacheProvider

/**
 * Created by wuzj on 15-2-1.
 */
class ObjectRedisCacheProvider implements CacheProvider{
    @Override
    Cache buildCache(Properties properties) {
        RedisCache redisCache = new CacheBuilder().newBuilder("redisCache").build()
        ObjectRedisCache objectRedisCache = new ObjectRedisCache()
        objectRedisCache.redisCache = redisCache
        Long ttl = properties.get("ttl")
        if (ttl > 0 )
            objectRedisCache.ttl = ttl
        return objectRedisCache
    }
}
