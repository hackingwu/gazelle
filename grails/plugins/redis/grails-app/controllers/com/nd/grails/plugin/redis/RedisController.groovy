package com.nd.grails.plugin.redis

import grails.converters.JSON
import grails.plugin.redis.RedisService
import redis.clients.jedis.Jedis

/**
 * @className RedisController
 * @author Su sunbin
 * @version 2014-10-20 创建 by Su sunbin
 */
class RedisController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	RedisService  redisService


	def index() {}

	def list(){
		JSON output
		Set<String> keys
		Set<String> members
		List<Map> datasToView = []
		redisService.withRedis {Jedis redis ->
			keys = redis.keys("*")
		}
		keys.each {key ->
			redisService.withRedis { Jedis redis ->
				members = redis.smembers(key)
			}
			members.each {member ->
				datasToView.add("key":key,"member":member)
			}
		}

		output = [success: true, message: "", data: datasToView] as JSON

		render output
	}

	/**
	 * 将一个或多个 member 元素加入到集合 key 当中
	 * @param key
	 * @param member
	 * @return
	 */
	JSON addMember(){
		JSON output
		long count = 0
		def key = params.key
		def member = params.member
		if(member != null && key != null){
				try {
					redisService.withRedis { Jedis redis ->
						count = redis.sadd(key,member)
					}
					if(count > 0){
						output = [success:true, message:"添加成功" ,data: count] as JSON
					}else {
						output = [success:false, message:"添加失败"] as JSON
					}
				} catch (Exception e) {
					log.error(e.getMessage())
					output = [success:false, message:"出现异常，添加失败"] as JSON
				}
		}else{
			output = [success:false, message:"请输入参数"] as JSON
		}

		render output
	}

	/**
	 * 根据key值，返回集合 key 的基数(集合中该key值对应的元素的数量)
	 * @param key
	 */
	JSON countMember(){
		JSON output
		long count = 0
		def key = params.key
		if(key != null){
			try {
				redisService.withRedis { Jedis redis ->
					count = redis.scard(key)
				}
				if(count > 0){
					output = [success:true, message:"记录条数为${count}", data:count] as JSON
				}else {
					output = [success:false, message:"记录不存在"] as JSON
				}
			} catch (Exception e) {
				log.error(e.getMessage())
				output = [success:false, message:"出现异常，查询失败"] as JSON
			}
		}else{
			output = [success:false, message:"请输入参数"] as JSON
		}

		render output
	}


	/**
	 * 判断 member 元素是否集合 key 的成员。
	 * @param key
	 * @param member
	 * @return
	 */
	JSON isMember(){
		JSON output
		boolean status = false
		def key = params.key
		def member = params.member
		if(key != null && member != null){
			try {
				redisService.withRedis { Jedis redis ->
					status = redis.sismember(key,member)
				}
				if(status == true){
					output = [success:true, message:"记录存在"] as JSON
				}else {
					output = [success:false, message:"记录不存在"] as JSON
				}
			} catch (Exception e) {
				log.error(e.getMessage())
				output = [success:false, message:"出现异常，查询失败"] as JSON
			}
		}else{
			output = [success:false, message:"请输入参数"] as JSON
		}

		render output
	}

	/**
	 * 返回集合 key 中的所有成员 不存在的 key 被视为空集合。
	 * @param key
	 * @return
	 */
	JSON showMembers(){
		JSON output
		Set<String> members
		def key = params.key
		if(key != null){
			try {
				redisService.withRedis { Jedis redis ->
					members = redis.smembers(key)
				}
				if(members.size() > 0){
					output = [success:true, message:"记录存在", data: members.toString()] as JSON
				}else {
					output = [success:false, message:"记录不存在"] as JSON
				}
			} catch (Exception e) {
				log.error(e.getMessage())
				output = [success:false, message:"出现异常，查询失败"] as JSON
			}
		}else{
			output = [success:false, message:"请输入参数"] as JSON
		}

		render output
	}

	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
	 * @param key
	 * @param member
	 * @return
	 */
	JSON deleteMember(){
		JSON output
		long count = 0
		def key = params.key
		def member = params.member
		if(member != null && key != null){
			try {
				redisService.withRedis { Jedis redis ->
					count = redis.srem(key,member)
				}
				if(count > 0){
					output = [success:true, message:"删除成功" ,data: count] as JSON
				}else {
					output = [success:false, message:"删除失败"] as JSON
				}
			} catch (Exception e) {
				log.error(e.getMessage())
				output = [success:false, message:"出现异常，删除失败"] as JSON
			}
		}else{
			output = [success:false, message:"请输入参数"] as JSON
		}

		render output
	}



}
