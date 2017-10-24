package com.nd.grails.plugin.redis

import com.nd.grails.plugin.redis.serializer.SerializeParameterizedType
import com.nd.grails.plugin.redis.serializer.StringSerializer
import com.nd.grails.plugin.redis.serializer.impl.DefaultStringSerializer
import com.nd.grails.plugin.redis.serializer.impl.GsonStringSerializer
import com.nd.grails.plugin.redis.serializer.impl.JsonSlurperStringSerializer
import grails.plugin.redis.RedisService
import redis.clients.jedis.Jedis

import java.lang.reflect.Type


class RedisCacheService extends RedisService {

    StringSerializer stringSerializer = new GsonStringSerializer(); //=  new GsonStringSerializer()//new DefaultStringSerializer()

    RedisCacheService withConnection(String connectionName){
        if(grailsApplication.mainContext.containsBean("redisCacheService${connectionName.capitalize()}")){
            return (RedisCacheService)grailsApplication.mainContext.getBean("redisCacheService${connectionName.capitalize()}")
        }
        if (log.errorEnabled) log.error("Connection with name redisService${connectionName.capitalize()} could not be found, returning default redis instead")
        return this
    }

    /**
     * 将缓存内容设置至redis中
      * @param key 键值
     * @param ttl 单位秒，过期时间设置，多少秒后过期
     * @param closure 返回需要设置的值
     * @return
     */
    def set(String key,Integer ttl,Closure closure){
        set(key,[expire:ttl],closure)
    }

    /**
     * 将缓存内容设置至redis中
     * @param key 键值 过期时间设置
     * @param options
     * @param closure
     * @return
     */
    def set(String key, Map options = [:] , Closure closure){
        Object result = closure()

        if(result){
               withOptionalRedis { Jedis redis ->
                   if (redis) {
                       if(options?.expire) {
                           redis.setex(key, options.expire, stringSerializer.serialize(result))
                       } else {
                           redis.set(key, stringSerializer.serialize(result))
                       }
                   }
               }
        }

    }

    def set(String key,Map options=[:],String value){
        if(value){
            withOptionalRedis { Jedis redis ->
                if (redis) {
                    if(options?.expire) {
                        redis.setex(key, options.expire, value)
                    } else {
                        redis.set(key, value)
                    }
                }
            }
        }
    }

    /**
     * 从缓存中取key的值,不存在返回空
     * @param key
     * @return
     */
//    public <T> T  get(String key){
//        String result = withOptionalRedis{ Jedis redis ->
//            if (redis) {
//                return redis.get(key)
//            }
//        }
//        if(result){
//            return stringSerializer.deserialize(result,T.class)
//        }
//        return null
//    }

    /**
     * 从缓存中取key的值,不存在返回空
     * @param key
     * @param cls
     * @return
     */
    public <T> T get(String key,Type type){
        String result = withOptionalRedis{ Jedis redis ->
                if (redis) {
                   return redis.get(key)
                }
        }

        if(result){
            return stringSerializer.deserialize(result,type)
        }
        return null
    }

    /**
     * 泛型集合处理
     * @param cls List.class、Map.class、Set.class
     * @param genericsType 集合中的泛型参数,若cls==Map，则为Map的Value值，key默认为String
     * @return
     */
    Type getGenericsType(Class cls, Class genericsType){
        if(cls  == Map.class){
//            Type[] types = new Type[2]
//            types[0] = String.class
//            types[1] = genericsType
            return new SerializeParameterizedType(cls,[String.class,genericsType].toArray(new Type[2]))
        }else {

            return new SerializeParameterizedType(cls,[genericsType].toArray(new Class[1]))
        }
    }

    /**
     * 泛型集合处理
     * @param cls List.class、Map.class、Set.class
     * @param genericsTypes Map处理
     * @return
     */
    Type getGenericsType(Class cls, Class[] genericsTypes){
        return new SerializeParameterizedType(cls,genericsTypes)

    }

	/**
	 * 将一个或多个 member 元素加入到集合 key 当中
	 * ，已经存在于集合的 member 元素将被忽略
	 * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合
	 * @param key
	 * @param value
	 * @return
	 */
	def sadd(String key,String value){
		withRedis {Jedis redis ->
			redis.sadd(key,value)
		}
	}

	/**
	 * 返回 key 所关联的字符串值。
	 * 如果 key 不存在那么返回特殊值 nil
	 * 假如 key 储存的值不是字符串类型，返回一个错误，因为 GET 只能用于处理字符串值。
	 * @param key
	 * @return
	 */
	String get(String key){
		withRedis {Jedis redis ->
			redis.get(key)
		}
	}



}
