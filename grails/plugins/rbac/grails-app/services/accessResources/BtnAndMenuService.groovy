package accessResources

import framework.ModelService
import framework.ValidateDomainService
import framework.biz.Page
import grails.transaction.Transactional
import grails.util.Holders
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.hibernate.SessionFactory
import test.ResourceBuilder

/**
 * @author :lch
 * @version 2014-11-12
 */
@Transactional(readOnly = true)
class BtnAndMenuService {
    static int ROOTID = 1
    protected Class<BtnAndMenu> persistentClass = null//(Class<T>) ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]
    protected String modelName

    private String countHql

    private String queryHql

    SessionFactory sessionFactory
    GrailsApplication grailsApplication

    public BtnAndMenuService(){
        this.persistentClass = BtnAndMenu.class

        char[] chars = BtnAndMenu.class.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()

        modelName = new String(chars)


        this.queryHql = " From " + this.persistentClass.getName()
        this.countHql = "Select count(*) From " + this.persistentClass.getName()

    }
    @Transactional
    Boolean initRoot(){
        String output
        if (this.findById(ROOTID) == null) {
            BtnAndMenu btnAndMenu = new BtnAndMenu()
            btnAndMenu.code = "root"
            btnAndMenu.isMenu = true
            btnAndMenu.title = btnAndMenu.label = "系统资源树"

            output = ValidateDomainService.validate(btnAndMenu)

            boolean validateSuccess = ((LinkedHashMap) new JsonSlurper().parseText(output)).get("success")
            if (validateSuccess){
                btnAndMenu.save()
//                BtnAndMenu.save(btnAndMenu)
            } else {
                log.info("资源项初始化失败")
                return false
            }
        }

        return true
    }
    @Transactional
    void initFromConfig(){
        //从config文件中读取配置
        List<LinkedHashMap> groupsFull = []
        LinkedHashMap<String, List> itemsFull = [:]
        def g = new GroovyClassLoader()
        def config = new ConfigSlurper().parse(g.loadClass('AuthConfig'))
        if(grailsApplication.config.cube.navigation.groups != null){
            groupsFull = grailsApplication.config.cube.navigation.groups
        }
        if(grailsApplication.config.cube.navigation.groups != null){
            itemsFull = grailsApplication.config.cube.navigation.items
        }
        if (groupsFull != null && !groupsFull.isEmpty()) {
            ResourceBuilder.btnConfigAnalyer(groupsFull)
        }
        LinkedHashMap<String, List> items = [:]

        //对预定义所有菜单组进行循环，找出允许显示的菜单项
        itemsFull.each { String key, List<LinkedHashMap<String, String>> value ->
            List menuItems = []
            for (int i = 0; i < value.size(); i++) {
                //在不考虑缓存的情况下，这部分直接从数据库里获取parentId = 1的全部资源，并与session["roles"]做对比，匹配则存入menuItems
                menuItems << value[i]
            }

            //对于包含多余1项的菜单组予以显示
            if (menuItems) {
                items[key] = menuItems
            }
            ResourceBuilder.btnConfigAnalyer(menuItems)

        }
    }
    @Transactional
    void initFromController(){
        //读取controller中的静态配置
        grailsApplication.controllerClasses.each { grailsClass ->
//            println grailsClass.logicalPropertyName
            List<LinkedHashMap> btnConfig = grailsClass.getPropertyValue("buttons")
            if (btnConfig != null && !btnConfig.isEmpty()) {
                ResourceBuilder.btnConfigAnalyer(btnConfig, grailsClass.logicalPropertyName)
            }
        }

    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    BtnAndMenu findById(Serializable id) {
        return persistentClass.get(id)
    }


    /**
     * 根据属性查找
     * @param properties 条件参数
     * @param page 分页参数,没有可以传空
     * @return
     */
    List<BtnAndMenu> findByProperties(Map<String, Object> properties, Page page = null){
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
    List<BtnAndMenu> findByProperties(Map<String, Object> properties, Page page,
                                      List<String> orderBys) {
        List<BtnAndMenu> l = null

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
    List<BtnAndMenu> list(Map params)
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
    def save(BtnAndMenu instance) throws Exception {
        instance.save()
    }

    /**
     * 根据id删除
     * @param id
     */
    @Transactional
    void deleteById(Serializable id) {
        BtnAndMenu instance = persistentClass.get(id)
        instance?.delete()
    }

}