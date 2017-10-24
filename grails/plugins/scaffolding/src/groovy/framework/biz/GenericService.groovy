package framework.biz

import framework.ModelService
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonSlurper

import org.hibernate.SQLQuery
import org.hibernate.SessionFactory

import java.util.Map.Entry


/**
 * 封装service公共统一方法
 * @author xfb
 * @since 2014/8/29
 * @version 2014/11/19 增加一个根据Map里的参数返回对应数据个数的方法countByProperties([id:1,role:"xxxx"])
 */
@Transactional(readOnly = true)
abstract class GenericService<T, ID extends Serializable> {


    protected Class<T> persistentClass = null//(Class<T>) ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]

    protected String modelName

    private String countHql

    private String queryHql

    SessionFactory sessionFactory

    public GenericService(Class<T> persistentClass){
        this.persistentClass = persistentClass

        char[] chars = persistentClass.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()

        modelName = new String(chars)


        this.queryHql = " From " + this.persistentClass.getName()
        this.countHql = "Select count(*) From " + this.persistentClass.getName()
    }

    /**
     * 保存数据
     * @param instance
     */
    @Transactional
    def save(T instance) throws Exception {
        instance.save()
    }


    /**
     * 批量保存数据
     * @param  userSearchable
     * @return
     */
    @Transactional
    def saveBatch(List<T> instances) throws Exception {
        if(instances && !instances.isEmpty()){
            instances.each {
                save(it)
            }
        }
    }

    /**
     *修改数据
     * @param instance
     * @return
     * @throws Exception
     */
    @Transactional
    def updateBath(List<T> instances)  throws Exception {
        if(instances && !instances.isEmpty()){
            instances.each {
                save(it)
            }
        }
    }

    /**
     *批量修改数据
     * @param instance
     * @return
     * @throws Exception
     */
    @Transactional
    def update(T instance) throws Exception {
        instance.save()
    }

    /**
     * 根据属性修改数据
     * @param instance
     * @param included if(true)只修改fields里的属性else{只修改不包含fields的属性}
     * @param fields
     * @return
     * @throws Exception
     */
    @Transactional
    def update(T instance,boolean included,Collection<String> fields) throws Exception {
        //TODO
        // persistentClass.get(instance.id),仅更新属性
        instance.save()
    }

    /**
     * 删除对象
     * @param userSearchable
     * @return
     */
    @Transactional
    def delete(T instance) throws Exception {
        instance.delete()
    }

    /**
     * 根据id删除
     * @param id
     */
    @Transactional
    void deleteById(Serializable id) {
        T instance = persistentClass.get(id)
        instance?.delete()
    }

    /**
     * 根据id列表批量删除
     * @param id
     */
    @Transactional
    void deleteByIds(List<ID> ids) {
       if(ids != null && !ids.isEmpty()){
            ids.each {
                T instance = persistentClass.get(it)
                instance?.delete()
            }
       }
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    T findById(Serializable id) {
        return persistentClass.get(id)
    }

    /**
     * 根据id列表查询对象
     * @param ids
     * @return
     */
    List<T> findByIds(List<Serializable> ids){
        if(ids != null && !ids.isEmpty()){
            return persistentClass.findAllByIdInList(ids)
        }
        return Collections.emptyList()
    }

    /**
     * 根据id列表查询对象
     * @param ids
     * @return
     */
    Map findByIdsReturnMap(List<Serializable> ids){
        Map map = [:]
        if(ids != null && !ids.isEmpty()){
            List<T> list = []
            list = persistentClass.findAllByIdInList(ids)
            list.each {
                map.put(it.id,it)
            }
        }
        return map

    }

    /**
     * 根据id列表查询对象
     * @param ids
     * @param ignoreNull
     * @return
     */
    List<T> findByIds(List<Serializable> ids,boolean ignoreNull){
        //TODO

    }

    /**
     *根据闭包条件查询数据
     * @param closure if(closure == null){cha}
     * @param page
     * @return
     */
    List<T> findAll(Closure closure,Page page = null){
        if(closure == null){
            closure = {}
        }

       return findAll([closure],page)
    }

    /**
     * 根据闭包条件查询数据
     * @param closures
     * @param page 分页参数
     * @return
     */
    List<T> findAll(List<Closure> closures,Page page = null){

        Closure c = null

        if(closures != null){
            if(closures.size() == 1){
                c = closures[0]
            }else {
                c = {
                    closures.each { closure ->
                        closure.setDelegate(c)
                        closure.call()
                    }
                }
            }
        }else {
            c = {}
        }

        List<T> r

        if(page != null){
            r = persistentClass.createCriteria().list([max:page.getMaxResults(), offset: page.getFirstResult()],c)

            if(page.isCount){
                page.setTotalResults( persistentClass.createCriteria().count(c).intValue())
            }
        }else {
            r = persistentClass.createCriteria().list(c)
        }
        return r
    }
    /**
     * 根据闭包查询，支持排序
     * @param closure 查询条件
     * @param page 分页对象
     * @param orderBys 排序条件
     * @return
     */
    List<T> findAll(Closure closure,Page page,List<String> orderBys){
        //合并条件查询和排序查询的闭包
        Closure orderClosure = getOrderClosure(orderBys)
        Closure c = {
            closure.call()
            orderClosure.call()
        }
        closure.setDelegate(c)
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        orderClosure.setDelegate(c)
        orderClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        findAll(c,page)
    }
    /**
     * 根据属性查找
     * @param properties 条件参数
     * @param page 分页参数,没有可以传空
     * @return
     */
    List<T> findByProperties(Map<String, Object> properties, Page page = null){
        return findByProperties(properties,page,null)
    }

    /**
     * 根据属性查找
     *
     * @param properties 条件参数
     * @param page 分页参数,没有可以传空
     * @param orderBys 排序字段 如 id desc , name,age asc
     * @return
     */
     List<T> findByProperties(Map<String, Object> properties, Page page,
                                    List<String> orderBys) {
         List<T> l = null

         Closure closure = {
             properties.each {key,value ->
                 eq key,value
             }
         }

         Closure orderBy = {
             if(orderBys != null){
                 orderBys.each {ord ->
                     String[] arr = ord.trim().split('\\s+')
                    if(arr.length > 1){
                        order(arr[0],arr[1])
                    }else {
                        order(ord)
                    }
                 }
             }
         }

         Closure c = {
             closure.call()
             orderBy.call()
         }

         closure.setDelegate(c)
         closure.setResolveStrategy(Closure.DELEGATE_FIRST)

         orderBy.setDelegate(c)
         orderBy.setResolveStrategy(Closure.DELEGATE_FIRST)

         if(page != null){

             l =  persistentClass.createCriteria().list([max:page.getMaxResults(), offset: page.getFirstResult()],c)

             if(page.isCount){
                 page.setTotalResults(persistentClass.createCriteria().count(closure).intValue())
             }

        }else {
             l =  persistentClass.createCriteria().list(c)
         }

        return l
    }

    /**
     * 计算总数
     * @param params
     * @return
     */
    long countByProperties(Map params)
    {
        Closure closure = {
            params.each {key,value ->
            eq key,value
            }
        }


        return Long.valueOf(persistentClass.createCriteria().count(closure).intValue())
    }

    /**
     * 基于hql查询，仅支持简单hql，不支持子查询
     * @param hql
     * @param args hql中参数对
     * @param page
     * @param properties 其他条件字段
     * @return
     */
    def findByHql(String hql,Map<String,Object> args,Page page,Map<String,Object> properties = null){

        return findByHql(hql,args,page,null,properties)
    }

    /**
     * 基于hql查询，仅支持简单hql，不支持子查询
     * @param hql
     * @param args hql中参数对
     * @param page
     * @param orders 排序参数
     * @param properties 其他条件字段
     * @return
     */
    def findByHql(String hql,Map<String,Object> args,Page page,List<String> orders, Map<String,Object> properties = null){
        if(args == null){
            args = [:]
        }

        StringBuilder sb = new StringBuilder(hql)

        if(properties != null){
            if(hql.toUpperCase().indexOf('WHERE') != -1){
                sb.append(' And ')
            }else {
                sb.append(' Where ')
            }

            sb.append(' ').append(buildWhere(properties))

            args.putAll(properties)

        }

        countByJPQL(sb.toString(),args,page)

        //增加排序条件
        if(orders != null && orders.size() > 0){
            sb.append(" order by ")

            orders.each { ord ->
                sb.append(ord).append(",")
            }

            sb.delete(sb.length()-1, sb.length())
        }

        // List l = persistentClass.executeQuery(sb.toString(),properties)
        if(page != null){
          return  persistentClass.executeQuery(sb.toString(),args,[max:page.maxResults, offset: page.firstResult])
        }else {
          return persistentClass.executeQuery(sb.toString(),args)
        }

    }

    /**
     * 基于sql查询，仅支持简单sql，不支持子查询
     * @param sql
     * @param args
     * @param page
     * @param properties
     * @return
     */
    def findBySQL(String sql,Map<String,Object> args,Page page,Map<String,Object> properties = null){
        return findBySQL(sql,args,page,null,properties)
    }

    /**
     * 通过sql语句查询
     * @param sql
     * @param args
     * @param page
     * @param orders
     * @param properties
     * @return
     */
    def findBySQL(String sql,Map<String,Object> args,Page page,List<String> orders, Map<String,Object> properties = null){

        StringBuilder sb = new StringBuilder(sql)

        if(properties != null){
            if(sql.toUpperCase().indexOf('WHERE') != -1){
                sb.append(' And ')
            }else {
                sb.append(' Where ')
            }

            sb.append(' ').append(buildWhere(properties))

            args.putAll(properties)

        }

        countBySQL(sb.toString(),args,page)

        //增加排序条件
        if(orders != null && orders.size() > 0){
            sb.append(" order by ")

            orders.each { ord ->
                sb.append(ord).append(",")
            }

            sb.delete(sb.length()-1, sb.length())
        }

        SQLQuery sqlQuery = sessionFactory.currentSession.createSQLQuery(sb.toString())

        if(args.size() > 0){
            args.each {String key,Object value ->
                sqlQuery.setParameter(key,value)
            }
        }

        if(page != null){
            sqlQuery.setMaxResults(page.maxResults)
            sqlQuery.setFirstResult(page.firstResult)
        }

        return sqlQuery.list()
    }

    /**
     *构造查询条件,默认表达式间关系为 And
     * @param properties
     * @return
     */
    private StringBuilder buildWhere(Map<String,Object> properties){
        StringBuilder sb = new StringBuilder()
        if(properties != null && !properties.isEmpty()){
            Iterator<Entry<String,Object>> it = properties.entrySet().iterator()

            //替换properties中key包含 点,如 obj.name
            Map<String,Object> tmp = new HashMap<String,Object>()

            while (it.hasNext()){
                Entry<String,Object> e = it.next()
                String key = e.key
                sb.append(key).append('= :')

                if(key.indexOf('.')){
                    key = key.replace('.','_')
                    tmp.put(key,e.getValue())
                    it.remove()
                }

                sb.append(key).append(' ')

                if(it.hasNext()){
                    sb.append(' And ')
                }
            }

            properties.putAll(tmp)

        }
        return sb
    }


    /**
     * 计算总数
     * @param params
     * @return
     */
    long count(Map params)
    {
        Map domain = ModelService.GetModel(this.modelName)

        return persistentClass.createCriteria().count(GetFilter().curry(domain, params, "count"))
    }

    /**
     * 获取分页数据
     * @param params
     * @return
     */
    List<T> list(Map params)
    {

        Map domain = ModelService.GetModel(this.modelName)
        return persistentClass.createCriteria().list([max: params.limit, offset: params.start] ,GetFilter().curry(domain, params, "list"))
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

            if (mode != "count" && p.sort) {
                List<Map> sort = new JsonSlurper().parseText(p.sort)
                sortProperty = sort[0].property
                sortDirection = sort[0].direction
            }

            if(p.search){
                List<Map> search = new JsonSlurper().parseText(p.search)

                for(Map record:search){
                    if ("startDate".equals(record.key)){
                        gt "dateCreated",new Date().parse("yyyy-MM-dd","${record.value}")
                    }
                    if ("endDate".equals(record.key)){
                        lt "dateCreated",new Date().parse("yyyy-MM-dd","${record.value}")+1
                    }
                    if (record.key && record.value &&d.fieldsMap[record.key]){
                        String type = d.fieldsMap[record.key].type
                        if (type == "string") {
                            ilike record.key, "%${record.value}%"
                            // eq record.key ,record.value
                        }else if (type == "int") {
                            eq record.key, Integer.parseInt(record.value)
                        }else if (type == "long") {
                            if (record.value.toString().contains(",")){
                                String[] data = record.value.toString().split(",")
                                if(data.size() >=2 && !"".equals(data[0]) && !"".equals(data[0])){
                                    gt record.key,Long.parseLong(data[0])
                                    lt record.key,Long.parseLong(data[1])
                                }
                            }else if(record.value.toString().contains("-")){
                                String[] data = record.value.toString().split("-")
                                if(data.size() >=2 && !"".equals(data[0]) && !"".equals(data[0])){
                                    gt record.key,Long.parseLong(data[0])
                                    lt record.key,Long.parseLong(data[1])
                                }
                            }else{
                                eq record.key, Long.parseLong(record.value)

                            }
                        }else if (type == "float") {
                            if (record.value.toString().contains(",")){
                                String[] data = record.value.toString().split(",")
                                if(data.size() >=2 && !"".equals(data[0]) && !"".equals(data[0])){
                                    gt record.key,Float.parseFloat(data[0])
                                    lt record.key,Float.parseFloat(data[1])
                                }
                            }else if(record.value.toString().contains("-")){
                                String[] data = record.value.toString().split("-")
                                if(data.size() >=2 && !"".equals(data[0]) && !"".equals(data[0])){
                                    gt record.key,Float.parseFloat(data[0])
                                    lt record.key,Float.parseFloat(data[1])
                                }
                            }else{
                                like record.key, Float.parseFloat(record.value)
                            }
                        }
                    }else if (record.key=='id' && record.value && record.value!='' ){ //支持基于ID查询，单选按钮使用
                        eq 'id', Long.parseLong(record.value)
                    }
                }
            }

            if (mode != "count" && p.sort) {
                order(sortProperty, sortDirection.toLowerCase())
            }
        }

        return c
    }

    /**
     * 计算总记录,只能处理简单jpql
     *
     * @param jpql
     * @param properties
     * @param page
     */
    private void countByJPQL(String jpql, Map<String, Object> properties,
                             Page page) {
        if (page != null && page.isCount) {
            String tmp = jpql.toUpperCase()
            StringBuilder sb = new StringBuilder(jpql)

            int from = tmp.indexOf("FROM")
            // int to = tmp.lastIndexOf("ORDER BY");
            sb.delete(0, from);
            sb.insert(0, "Select count(*) ")

           List l = persistentClass.executeQuery(sb.toString(),properties)

            if( l != null && l.size() > 0){
               page.setTotalResults(l.get(0).intValue())
           }

        }
    }

    /**
     * 根据SQL计算总数
     * @param sql
     * @param properties
     * @param page
     */
    private void countBySQL(String sql,Map<String, Object> properties,
                            Page page){
        if (page != null && page.isCount) {
            String tmp = sql.toUpperCase()
            StringBuilder sb = new StringBuilder(sql)

            int from = tmp.indexOf("FROM")
            // int to = tmp.lastIndexOf("ORDER BY");
            sb.delete(0, from);
            sb.insert(0, "Select count(*) ")

            SQLQuery sqlQuery = sessionFactory.currentSession.createSQLQuery(sb.toString())

            if(properties != null && properties.size() > 0){
                properties.each {String key,Object value ->
                    sqlQuery.setParameter(key,value)
                }
            }

            page.totalResults = ((Number)sqlQuery.uniqueResult()).intValue()
        }
    }


    private String buildHQL(Map<String, Object> properties) {
        return buildHQL(properties, false, null)
    }

    /**
     * 构造单表查询hql语句
     * @param properties
     * @param isCount
     * @param orderBys
     * @return
     */
    private String buildHQL(Map<String, Object> properties, boolean isCount,
                             List<String> orderBys ) {
        if (properties == null || properties.isEmpty()) {
            if (isCount) {
                return this.countHql
            }

            if (orderBys == null || orderBys.size() == 0) {
                return this.queryHql
            }
        }
        StringBuilder sb = new StringBuilder(400)

        if (isCount) {
            sb.append(this.countHql)
        } else {
            sb.append(this.queryHql)
        }

        if (properties != null && !properties.isEmpty()) {
            sb.append(" Where ")
            Iterator<String> it = properties.keySet().iterator()
            String name = null
            while (it.hasNext()) {
                name = it.next()
                sb.append(name).append("=:").append(name)
                if (it.hasNext()) {
                    sb.append(" And ")
                }
            }
        }

        if (orderBys != null && orderBys.size() > 0) {
            sb.append(" order by ")

            orderBys.each { ord ->
                sb.append(ord).append(",")
            }

            sb.delete(sb.length()-1, sb.length())

        }
        return sb.toString()
    }

    /**
     * 全文检索
     * @param params
     * @return
     */
    Map search(Map params){
        Map domain = ModelService.GetModel(this.modelName)
        List<Map> search = new JsonSlurper().parseText(params.search)

        Map r = persistentClass.search().list {

            for(Map record:search){
                if (record.key && record.value &&domain.fieldsMap[record.key]){
                    keyword record.key,record.value
                }
            }

            sort "id", "asc"

            maxResults Long.valueOf(params.limit).intValue()

            offset Long.valueOf(params.start).intValue()
        }
        return r
    }


    Closure getPropertiesClosure(Map<String,Object> properties){
        Closure closure = {
            properties.each {key,value->
                eq key,value
            }
        }
        return closure
    }
    Closure getOrderClosure(List<String> orderBys){
        Closure orderBy = {
            if (orderBys != null){
                orderBys.each {ord->
                    String[] arr = ord.trim().split('\\s+')
                    if (arr.length > 1){
                        order arr[0],arr[1]
                    }else{
                        order ord
                    }
                }
            }
        }
        return orderBy
    }

}
