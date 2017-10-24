package com.nd.grails.plugins.log

import com.nd.grails.plugins.log.loginterface.LogSaveInter
import grails.converters.JSON

import java.util.concurrent.LinkedBlockingQueue

/**
 * @author lch
 * @version 2014/8/21 实现了把日志内容暂时缓存在内存队列内，并且提供了两个存，一个取，以及一个查询条数的接口
 */
class MemoryLogService implements LogSaveInter{
    LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<String>();


    //存入缓存接口
    Boolean add (Log log){
        String logJson = log as JSON
        linkedBlockingQueue.put(logJson)
    }
    //存入缓存接口，输入的为map转Json后的值
    Boolean add (String json){
        linkedBlockingQueue.put(json)

    }
    //从缓存取出接口
    String get (){
        return linkedBlockingQueue.poll()
    }



    //获取队列长度接口
    int getQueueLength(){
        return linkedBlockingQueue.size()

    }

    def serviceMethod() {

    }
}
