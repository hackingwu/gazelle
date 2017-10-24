package com.nd.grails.plugins.log.redis

import grails.converters.JSON
import grails.plugin.redis.RedisService
import grails.transaction.Transactional
import redis.clients.jedis.Jedis

@Transactional
class RedisExportQueueService {

    static transactional = false
    def grailsApplication
    RedisService redisService

    JSON logExport(String export){
        try{
            redisService.withRedis {Jedis redis->
                redis.rpush(grailsApplication.config.com.dd.log.redisKey.exportFile,export)
                return ([success:true,message:"导出成功"] as JSON)
            }
        }catch (Exception e){
            return ([success:false,message:"导出失败"] as JSON)
        }
    }
}
