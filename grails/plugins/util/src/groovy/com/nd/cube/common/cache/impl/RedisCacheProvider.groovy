package com.nd.cube.common.cache.impl

import com.nd.cube.common.cache.Cache
import com.nd.cube.common.cache.CacheProvider
import framework.util.StringUtil
import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

/**
 * Created by wuzj on 2015/1/28.
 */
class RedisCacheProvider implements CacheProvider{
    @Override
    Cache buildCache(Properties properties) {
        def redisService
        String redisServiceName = properties.get("redisService")
        Long ttl = Long.valueOf(properties.get("ttl"))
        if (!StringUtil.isEmpty(redisServiceName)){
            redisService = Holders.grailsApplication.getMainContext().getBean(redisServiceName)
        }
        if (redisService == null){
            Map redisConfigMap = properties.get("redisConfigMap")
        }
        RedisCache redisCache = new RedisCache()
        redisCache.redisService = redisService
        if (ttl > 0)
            redisCache.ttl = ttl
        return redisCache
    }
}
