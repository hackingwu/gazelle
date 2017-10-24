package com.nd.grails.plugins.log.loginterface

import com.nd.grails.plugins.log.Log

/**
 * @author lch
 * @version 2014/8/21 新增一个String类型的接口
 */
public interface LogSaveInter {
    //存入缓存接口
    Boolean add (Log log)
    //存入缓存接口，输入的为map转Json后的值
    Boolean add (String json)
    //从缓存取出接口
    String get ()
    //获取队列长度接口



    //获取队列长度接口
    int getQueueLength()
}