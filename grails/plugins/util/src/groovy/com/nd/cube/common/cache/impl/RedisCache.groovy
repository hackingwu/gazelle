package com.nd.cube.common.cache.impl

import com.nd.cube.common.cache.Cache
import grails.plugin.redis.RedisService
import redis.clients.jedis.Jedis

/**
 * Created by wuzj on 2015/1/28.
 */
class RedisCache implements Cache<String,String>{
    long ttl = -1L;
    int continueTime = 60*5;
    RedisService redisService
    @Override
    java.lang.String get(String key) {
        String value = null
        try{
            redisService.withRedis {Jedis redis->
                value = redis.get(key)
                if (value!=null&&redis.ttl(key)<continueTime){
                    redis.expire(key,continueTime)
                }
            }
        }catch (Exception e){

        }
        return value
    }

    @Override
    void put(String key, String value) {
        redisService.withRedis {Jedis redis->
            if (ttl > 0){
                redis.set(key,value)
                redis.expire(key,(int)ttl)
            }
            else{
                redis.set(key,value)
            }
        }
    }

    @Override
    void put(String key, String value, long expire) {
        redisService.withRedis {Jedis redis->
            if (expire > 0 ){
                redis.set(key,value)
                redis.expire(key,(int)expire)
            }
            else{
                redis.set(key,value)
            }
        }
    }

    @Override
    void putIfAbsent(String key, String value) {
        if (value != null){
            put(key,value)
        }
    }

    @Override
    void putIfAbsent(String key, String value, long expire) {
        if (value != null){
            put(key,value,expire)
        }
    }

    @Override
    boolean containsKey(String key) {
        Boolean flag
        redisService.withRedis {Jedis redis->
            flag = redis.exists(key)
        }
        return flag
    }

    @Override
    void remove(String key) {
        redisService.withRedis {Jedis redis->
            redis.del(key)
        }
    }

    @Override
    void invalidate(String key) {
        redisService.withRedis {Jedis redis->
            redis.del(key)
        }
    }

}
