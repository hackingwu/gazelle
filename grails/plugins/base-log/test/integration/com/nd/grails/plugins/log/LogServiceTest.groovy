package com.nd.grails.plugins.log

import grails.converters.JSON

/**
 * 日志服务测试
 * @since 2014/8/4.
 */
class LogServiceTest extends GroovyTestCase {

    static transactional = false
    LogService logService;
    RedisLogService redisLogService
    MemoryLogService memoryLogService
    void setUp(){




    }

    void tearDown(){
        log.error "tearDown"

    }
    /**
     * 写入队列
     */

    void testRedisAdd(){
      logService.Init(CacheType.redis)
      int count = logService.size()
      Log log = createLog()
      logService.info(log)

      String str = log as JSON
      logService.saveInQueueLog(str)

      Map param = [logMsg:"map测试"]
      logService.info(param)

        logService.info {Log it ->
            it.logMsg = "这是一个闭包测试"

        }

      assert count+4 == logService.size()


    }

    /**
     * 写入队列
     */
    void testMemoryAdd() {
        logService.Init(CacheType.memory)
        int count = logService.size()
        Log log = createLog()
        logService.info(log)

        String str = log as JSON
        logService.saveInQueueLog(str)

        Map param = [logMsg:"map测试"]
        logService.info(param)

        logService.info {Log it ->
            it.logMsg = "这是一个闭包测试"

        }

        assert count+4 == logService.size()

    }

    void testThreads(){

        logService.Init(CacheType.redis)
        int count = logService.size()
        int THREADS_NUM=100
        int NUM_OF_CREATE=10
        List<Thread> threads=[]
        for(int i=0;i<THREADS_NUM;i++)
        {
            threads << Thread.start{
                for(int j=0; j<NUM_OF_CREATE; j++) {

                    logService.info {Log log ->
                        log.logMsg = "这是一个多线程测试"
                    }
                }
            }
        }
        threads*.join()
        assert logService.size() == count+1000

        println "多线程创建"
    }



     Log createLog(){

        Log log = Log.build(ip:"192.168.57.208")
        return log
    }



}
