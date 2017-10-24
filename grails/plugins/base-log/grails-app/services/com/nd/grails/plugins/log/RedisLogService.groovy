package com.nd.grails.plugins.log

import com.nd.grails.plugins.log.loginterface.LogSaveInter
import grails.converters.JSON
import grails.plugin.redis.RedisService
import redis.clients.jedis.Jedis

/**
 * @author lch
 * @version 2014/8/21 新增一个输入参数为String 类型的add接口
 * 实现了把日志内容暂时缓存在内存队列内，并且提供了两个存，一个取，以及一个查询条数的接口
 */

class RedisLogService implements LogSaveInter{
    RedisService redisService

    def grailsApplication
//日志插入内存队列

    Boolean add(Log log) {
        if (log != null) {
            String json = log as JSON

            saveInRedis(json)
            return true
        } else {
            return false
        }
    }

    Boolean add(String json){
        if (json != null) {

            saveInRedis(json)
            return true
        } else {
            return false
        }
    }
//日志从内存队列取出
    String get() {
        return getFromRedis()

    }


    Boolean saveInRedis(String json) {
        try {
            redisService.withRedis { Jedis redis ->
                redis.rpush(grailsApplication.config.com.dental_doctor.redisKey.log, json) //保存日志信息，使用队列list

            }
            return true
        } catch (Exception e) {
            log.error(e.getMessage())
            return false
        }
    }

    String getFromRedis() {
        try {
            redisService.withRedis { Jedis redis ->
                String logJson = "";
                if ((logJson = redis.lpop(grailsApplication.config.com.dental_doctor.redisKey.log)) != null && !logJson.isEmpty()) {
                    return logJson
                }
                return null
            }

        } catch (Exception e) {
            log.error "redis取出日志错误" + e.getMessage()
            return null
        }
    }

    int getQueueLength(){
        redisService.withRedis { Jedis redis ->
            return redis.llen(grailsApplication.config.com.dental_doctor.redisKey.log)
        }
    }

    def serviceMethod() {

    }
}





