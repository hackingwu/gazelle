package com.nd.grails.plugins.log

import com.nd.grails.plugins.log.loginterface.LogSaveInter

import framework.ModelService
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonSlurper

import java.text.SimpleDateFormat


/**
 * 日志记录服务主要记录日志操作记录
 *
 * @author lch
 * @version 2014/8/27以选择缓存方式有redis或内存队列两种，修改一个loglist队列未清空导致的bug,去掉多余的println,将必要的信息用log.info打印到控制台，增加save时捕获异常的操作，避免线程崩溃
 * @version 2014/9/29将对象名冲突的部分先去掉
 * @version 2014/9/18 日志IP判空操作
 */
//使用时调用Init方法，传入任意一种缓存机制对应的枚举元素
public enum CacheType{redis,memory}
class LogService {
    def sessionFactory

    //线程安全的
    static ThreadLocal<Map> threadLocal = new ThreadLocal<Map>()

    RedisLogService redisLogService
    MemoryLogService memoryLogService
    //日志缓存接口
    LogSaveInter logSaveInter //MemoryLogService
//用来读取缓存并存入数据库的线程
    Thread logInsertSqlThread = null

    //一次性从redis中取出的条数，即一次性保存到数据库中的条数
    static int ONCESAVENUMBER = 10

/**
 * 初始化函数，要来初始化缓存机制，和读取缓存的线程
 * @param cacheType
 * @return
 */
    Boolean Init(CacheType cacheType) {

        switch (cacheType){
            case CacheType.redis:
                logSaveInter = redisLogService
                break
            case CacheType.memory:
                logSaveInter = memoryLogService
                break
            default://默认采用内存来缓存日志信息
                logSaveInter = memoryLogService
                break
        }
        try {
            if (logInsertSqlThread == null) {

                log.info("创建读日志取队列线程")
                logInsertSqlThread = Thread.start {
                    insertSqlThread()
                }
            }
            return true
        } catch (Exception e) {
            log.info("林畅辉写的日志服务启动出错啦" + e.getMessage())
            return false
        }
    }

    /**
     * 日志写入
     * @param log
     */

    public Boolean saveInQueueLog(Log log) {
        try {
            log.ip = "无"
            if(threadLocal.get() != null){
                Map m = threadLocal.get()
                if (m.containsKey("ip")){
                    log.ip = m.get("ip")
                }
                if (m.containsKey("name")&&log.operatorName==null){
                    log.operatorName = m.get("name")
                }
            }
            if (log.operatorName == null) log.operatorName = "游客"
            log.birthday = new Date()

//            logSaveInter.add(logJson)提供logJson的存储方式
            logSaveInter.add(log)

//            println "插入后队列内任务个数"+size()
            return true

        } catch (Exception e) {
//            log.info e.getMessage()
            return false
        }
    }

    /**
     * 日志写入
     *
     * @param 输入的参数为map转json,例如{"logMsg":"xxxxx"}
     */

    public Boolean saveInQueueLog(String logJson) {
        try {
            JsonSlurper jsonSlurper = new JsonSlurper()
            Map param = jsonSlurper.parseText(logJson)
            Log log = new Log(param)
            log.ip = "无"
            if(threadLocal.get() != null){
                Map m = threadLocal.get()
                if (m.containsKey("ip")){
                    log.ip = m.get("ip")
                }
                if (m.containsKey("name")&&log.operatorName==null){
                    log.operatorName = m.get("name")
                }
            }
            log.birthday = new Date()
            String str = (log as JSON)

//            logSaveInter.add(logJson)提供logJson的存储方式
            logSaveInter.add(str)

//            println "插入后队列内任务个数"+size()
            return true

        } catch (Exception e) {
            log.error e.getMessage()
            return false
        }
    }

    /**
     * 日志列表内容插入数据库
     *
     */
    public synchronized insertSqlThread() {
        List<Map> logList = []
        while (true) {
            try {
                if (logSaveInter.getQueueLength() >= ONCESAVENUMBER) {
                    JsonSlurper jsonSlurper = new JsonSlurper()
                    for(int i=0;i< ONCESAVENUMBER;i++){
                        String logJson = logSaveInter.get()
                        Map params = jsonSlurper.parseText(logJson);
                        log.info("insertSqlThread"+params)
                        if (params != null) {
                            logList.add(params)
                        }
//                        println "取出后队列内任务个数"+size()
                    }
                    if(logList != null){
                        save(logList)
                        logList = []
                    }
                }
//                println "当前缓存内存在的条数"+size()
            } catch (InterruptedException e) {
                log.error "存入数据库过程中出错"+e.getMessage()
            }
            sleep(5000)
        }
    }

    /**
     * 手动清空缓存内的日志，存入数据库内
     *
     */
    public cacheEmpty() {
        log.info("手动清空cache内未被插入数据库的日志服务")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        List<Map> logList = []
        JsonSlurper jsonSlurper = new JsonSlurper()
        while (size()!= 0){
            String logJson = logSaveInter.get()
            Map params = jsonSlurper.parseText(logJson);
//            println params
            if (params.birthday && params.birthday instanceof String){
                params.birthday = simpleDateFormat.parse(params.birthday)
            }
            try{
                Log log = new Log(params)

                save(log)
            }catch (Exception e){
                log.error e.getMessage()
            }

        }
    }
    /**
     *返回当前缓存中存在的日志条数
     *
     */
    int size(){
//        logSaveInter = redisLogService
        return logSaveInter.getQueueLength()
    }

    /**
     * 记录日志信息
     * @param log
     */
    Boolean info(Log log) {
        log.level = 1
        
        return saveInQueueLog(log)

    }

    /**
     * 记录日志信息
     * @param log
     */
    Boolean warn(Log log) {
        log.level = 2
        return  saveInQueueLog(log)

    }

    /**
     * 记录日志信息
     * @param log
     */
    Boolean error(Log log) {
        log.level = 3
        return saveInQueueLog(log)

    }

    /**
     * 记录日志信息
     * @param log
     */
    Boolean info(Map log) {
        if(log != null){
            log.level = 1
            return saveInQueueLog(new Log(log))
        }else{
            return false
        }

    }

    /**
     * 记录日志信息
     * @param log
     */
    def warn(Map log) {
        if(log != null){
            log.level = 2
            return saveInQueueLog(new Log(log))
        }else{
            return false
        }
    }

    /**
     * 记录日志信息
     * @param log
     */
    def error(Map log) {
        if(log != null){
            log.level = 3
            return saveInQueueLog(new Log(log))
        }else{
            return false
        }
    }

    /**
     * 记录日志信息
     * @param log
     */
    def info(Closure log) {

        Log log1 = new Log()
        log1.level = 1
        log.call(log1)
        return saveInQueueLog(log1)

    }

    /**
     * 记录日志信息
     * @param log
     */
    def warn(Closure log) {
        Log log1 = new Log()
        log1.level = 2
        log.call(log1)
        return saveInQueueLog(log1)

    }

    /**
     * 记录日志信息
     * @param log
     */
    def error(Closure log) {
        Log log1 = new Log()
        log1.level = 3
        log.call(log1)
        return saveInQueueLog(log1)


    }

    List<Log> search(Closure closure, Map args) {


    }


    /**
     * 保存数据
     * @param log
     */
    @Transactional
    def save(List<Map> logList) throws Exception {
        //
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        logList.each {
            try{
                if (it.birthday && it.birthday instanceof String){
                    it.birthday = simpleDateFormat.parse(it.birthday)
                }
                new Log(it).save()
            }catch (Exception e){
                log.error "捕获异常避免日志线程崩溃"+e.getMessage()
            }
        }

    }
    /**
     * 保存数据
     * @param log
     */
    @Transactional
    def save(Log log) throws Exception {
        //
        try{
            log.save();
        }catch (Exception e){
            log.error "捕获异常避免日志线程崩溃"+e.getMessage()
        }

    }

    /**
     * 根据id删除
     * @param log
     * @return
     */
    @Transactional
    def delete(Log log) throws Exception {
        log.delete()
    }

    @Transactional
    void deleteById(Long id) {
        Log log = Log.get(id)
        log?.delete()
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    Log findById(Serializable id) {
        return Log.get(id);
    }

    /**
     * 计算总数
     * @param params
     * @return
     */
    long count(Map params)
    {
        Map domain = ModelService.GetModel("log")

        return Log.createCriteria().count(GetFilter().curry(domain, params, "count"))
    }

    /**
     * 获取分页数据
     * @param params
     * @return
     */
    List<Log> list(Map params)
    {
        Map domain = ModelService.GetModel("log")

        return Log.createCriteria().list([max: params.limit, offset: params.start] ,GetFilter().curry(domain, params, "list"))
    }

    /**
     * 获取计算闭包
     * @return
     */

    Closure GetFilter()
    {
        Closure c = { Map d, Map p, String mode ->
            String sortProperty
            String sortDirection
            if(p.sort==null){
                p<<["sort":'[{"property":"birthday","direction":"DESC"}]']
            }
            if (mode != "count" && p.sort) {
                List<Map> sort = new JsonSlurper().parseText(p.sort)
                sortProperty = sort[0].property
                sortDirection = sort[0].direction
            }

            if(p.search){
                List<Map> search = new JsonSlurper().parseText(p.search)

                for(Map record:search){
                    if (record.key && record.value &&d.fieldsMap[record.key]){
                        String type = d.fieldsMap[record.key].type
                        if (type == "string") {
                            ilike record.key, "%${record.value}%"
                        }else if (type == "int") {
                            eq record.key, Integer.parseInt(record.value)
                        }else if (type == "long") {
                            eq record.key, Long.parseLong(record.value)
                        }else if (d.fieldsMap[record.key].widget == "datefield"){
                            eq record.key, new Date().parse("yyyy-MM-dd","${record.value}")
                        }else if (d.fieldsMap[record.key].widget == "datetimefield"){
//	                        eq record.key, new Date().parse("yyyy-MM-dd HH:mm:ss","${record.value}") //按照“年-月-日 时：分：秒”精确查询，需要在ExtTagLib中调用'datetimefield'控件
                            between (record.key,new Date().parse("yyyy-MM-dd","${record.value}"),new Date().parse("yyyy-MM-dd","${record.value}")+1)//使用时间范围，按照“年-月-日”模糊查询所有当天的所有记录
                        }
                    }
                }
            }

            if (mode != "count" && p.sort) {
                order(sortProperty, sortDirection.toLowerCase())
            }
        }

        return c
    }

    List<Map<String, String>> getExportLogList(int pageNo, int pageSize){
        final session = sessionFactory.currentSession
        String query = 'select catalog,log_msg,birthday,ip,operator,operator_name from log'
        int limit = pageNo * pageSize

        if(limit < 0){
            limit = 0
        }
        final sqlQuery = session.createSQLQuery(query).setFirstResult(limit).setMaxResults(pageSize)
        final queryResults = sqlQuery.with{
            list()
        }

        //顺序必须与excel列对齐
        final results = queryResults.collect { resultRow ->
            [
                operator    : resultRow[4],
                operatorName: resultRow[5],
                catalog     : resultRow[0],
                logMsg      : resultRow[1],
                ip          : resultRow[3],
                birthday    : resultRow[2]
            ]
        }

        return results
    }

}
