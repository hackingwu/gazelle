package  relation

import accessResources.BtnAndMenu
import framework.ValidateDomainService
import framework.ViewOperationService
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.InitializingBean

/**
 *
 * @author Administrator
 * @since  2014-11-17
 */
class RoleAccessRelationController implements InitializingBean{
    def roleAccessRelationService
    def authService
    protected Class<RoleAccessRelation> persistentClass = null//(Class<T>) ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]

    protected String modelName

    private String serviceName

    ViewOperationService viewOperationService

    def service

    def exportService

    def excelImportService

    GrailsApplication grailsApplication
    public RoleAccessRelationController(){
//        super(RoleAccessRelation.class)
        this.modelName = "roleAccessRelation"

    }

    /**
     * AUTO: 静态列表(数据通过JSON方式加载)
     */
    def index() {

    }

    /**
     * AUTO: 提供列表数据
     */
    def list() {
        String output
        try {
            long count = roleAccessRelationService.count(params)
            List<RoleAccessRelation> datas = roleAccessRelationService.list(params)

            List<Map> datasToView = []

            for(int i=0;i<datas?.size();i++)
            {
                datasToView << viewOperationService.ConvertToView(this.modelName, datas[i])
            }

            output = [success: true, message: "", totalCount: count, data: datasToView] as JSON
        }catch (e){
            log.error('',e)
            output = [success: false, message: "请输入正确的查询条件！"] as JSON
        }
        render output
    }

    /**
     * AUTO: 创建实例
     */
    def createAction() {
        Map paramsMap = viewOperationService.ConvertFromView(this.modelName, params)

        roleAccessRelationService.save( persistentClass.newInstance([paramsMap] as Object[]))

        render([success: true, message: "创建成功!"] as JSON)
    }

    /**
     * AUTO: 明细
     */
    def detailAction(){
        RoleAccessRelation address = roleAccessRelationService.findById(params["id"])

        String output = [
                success: true,
                message: "",
                data: viewOperationService.ConvertToView(this.modelName, address)
        ] as JSON

        render output
    }

    /**
     * AUTO: 更新
     */
    def updateAction(){
        RoleAccessRelation address = roleAccessRelationService.findById(params["id"])

        address.properties = viewOperationService.ConvertFromView(this.modelName, params)

        roleAccessRelationService.save(address)

        String output = [success: true, message: "更新成功!"] as JSON
        render output
    }

    /**
     * AUTO：删除
     */
    def deleteAction(){
        List<Long> ids=params.data.split(",").collect{Long.parseLong(it)}

        ids.each {
            roleAccessRelationService.deleteById(it)
        }

        String output = [success: true, message: "删除成功!"] as JSON
        render output
    }

    /**
     * 权限树的修改和保存
     */
    def save() {

        String output = ([success: true, message: "操作成功!"] as JSON)
        List<Long> btnAndMenuIds = []

        List<RoleAccessRelation> roleAccessRelations = []

        long roleId
        if (params.roleId == null) {
            render([success: false, message: "请选择一条角色!"] as JSON)
            return
        }

        if (params.resourceIds == null) {
            //todo 删除对应的roleId在RoleAccessRelation表中的全部记录
            roleAccessRelations = roleAccessRelationService.findAll({ eq "sysRoleId", roleId })
            roleAccessRelations.each { RoleAccessRelation it ->
                roleAccessRelationService.delete(it)
            }
            render([success: true, message: "操作成功!"] as JSON)
            return
        }

        params.resourceIds.toString().split(',').each {
            if (it != "") {
                btnAndMenuIds.add(Long.valueOf(it))

            }
        }
//            btnAndMenuIds = params.resourceIds //这里有要特殊处理单传过来的资源id只有一个的情况时
        roleId = Long.parseLong(params.roleId)

        List<RoleAccessRelation> saveList = []
        List<Long> deleteList = []

        if (roleAccessRelationService.countByProperties([sysRoleId: roleId]) == 0) {
            //todo 这个角色在数据库中还没有记录，直接全部新建
            btnAndMenuIds.each { it ->
                RoleAccessRelation roleAccessRelation = new RoleAccessRelation()
                roleAccessRelation.sysRoleId = roleId
                roleAccessRelation.btnAndMenuId = Long.parseLong(it.toString())
                output = ValidateDomainService.validate(roleAccessRelation)
                boolean validateSuccess = ((Map) new JsonSlurper().parseText(output)).get("success")
                if (validateSuccess) {
//                    roleAccessRelationService.save(roleAccessRelation)
                    saveList.add(roleAccessRelation)
                    authService.addCaching(roleAccessRelation.sysRoleId,roleAccessRelation.btnAndMenuId)
                }
            }
            roleAccessRelationService.saveBatch(saveList)
            render output
            return
        }
        if (roleAccessRelationService.countByProperties([sysRoleId: roleId]) != 0) {
            //todo 牛逼的算法！修改角色信息，对（旧集合-（旧集合和新集合））的部分删除，对（新集合-（旧集合和新集合））的部分新增
            btnAndMenuIds.each { it ->
                if (roleAccessRelationService.countByProperties([sysRoleId: roleId, btnAndMenuId: Long.valueOf(it)]) == 0) {
                    RoleAccessRelation roleAccessRelation = new RoleAccessRelation()
                    roleAccessRelation.sysRoleId = roleId
                    roleAccessRelation.btnAndMenuId = Long.parseLong(it.toString())
                    output = ValidateDomainService.validate(roleAccessRelation)
                    boolean validateSuccess = ((Map) new JsonSlurper().parseText(output)).get("success")
                    if (validateSuccess) {
//                        roleAccessRelationService.save(roleAccessRelation)
                        saveList.add(roleAccessRelation)
                        authService.addCaching(roleAccessRelation.sysRoleId,roleAccessRelation.btnAndMenuId)
                    }
                }
            }
            roleAccessRelationService.saveBatch(saveList)

            roleAccessRelationService.findAll({ eq "sysRoleId", roleId }).each { RoleAccessRelation it ->
                //删除旧集合-（旧集合和新集合）

                if (btnAndMenuIds.indexOf(it.btnAndMenuId) == -1) {
//                    roleAccessRelationService.delete(it)
                    deleteList.add(it.id)
                    authService.deleteCaching(it.sysRoleId,it.btnAndMenuId)
                }
            }
            roleAccessRelationService.deleteByIds(deleteList)

        }

        render output
    }

    @Override
    void afterPropertiesSet() throws Exception {
        this.persistentClass = RoleAccessRelation.class

        char[] chars = persistentClass.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()

        modelName = new String(chars)

        serviceName = "RoleAccessRelationService"

        roleAccessRelationService = (RoleAccessRelationService)grailsApplication.mainContext.getBean("${modelName}Service")
    }
}