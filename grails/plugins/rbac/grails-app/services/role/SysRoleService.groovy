package role

import accessResources.BtnAndMenu
import framework.ModelService
import framework.biz.Page
import grails.transaction.Transactional
import groovy.json.JsonSlurper

/**
 * @author :lch
 * @version 2014-11-17
 */
@Transactional(readOnly = true)
class SysRoleService {

    protected Class<SysRole> persistentClass = null//(Class<T>) ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]
    protected String modelName

    private String countHql

    private String queryHql
    public SysRoleService(){
        this.persistentClass = SysRole.class

        char[] chars = SysRole.class.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()

        modelName = new String(chars)


        this.queryHql = " From " + this.persistentClass.getName()
        this.countHql = "Select count(*) From " + this.persistentClass.getName()

    }


    /**
     *根据闭包条件查询数据
     * @param closure if(closure == null){cha}
     * @param page
     * @return
     */
    List<SysRole> findAll(Closure closure,Page page = null){
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
    List<SysRole> findAll(List<Closure> closures,Page page = null){

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

        List<SysRole> r

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
    List<SysRole> list(Map params)
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
                    if (record.key && record.value &&d.fieldsMap[record.key]){
                        String type = d.fieldsMap[record.key].type
                        if (type == "string") {
                            ilike record.key, "%${record.value}%"
                            // eq record.key ,record.value
                        }else if (type == "int") {
                            eq record.key, Integer.parseInt(record.value)
                        }else if (type == "long") {
                            eq record.key, Long.parseLong(record.value)
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
     * 保存数据
     * @param instance
     */
    @Transactional
    def save(SysRole instance) throws Exception {
        instance.save()
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    SysRole findById(Serializable id) {
        return persistentClass.get(id)
    }

    /**
     * 根据id删除
     * @param id
     */
    @Transactional
    void deleteById(Serializable id) {
        SysRole instance = persistentClass.get(id)
        instance?.delete()
    }

    /**
     * 根据属性查找
     * @param properties 条件参数
     * @param page 分页参数,没有可以传空
     * @return
     */
    List<SysRole> findByProperties(Map<String, Object> properties, Page page = null){
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
    List<SysRole> findByProperties(Map<String, Object> properties, Page page,
                                List<String> orderBys) {
        List<SysRole> l = null

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


}