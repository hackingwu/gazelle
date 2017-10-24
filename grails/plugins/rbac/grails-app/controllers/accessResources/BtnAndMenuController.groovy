package  accessResources

import framework.ViewOperationService
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import relation.RoleAccessRelation
import relation.RoleAccessRelationService

/**
 *
 * @author Administrator
 * @since  2014-11-12
 */
class BtnAndMenuController {
    static buttons = [
            //这些配在配置管理里的页面，都需要在对应的controller里去配置一个页面
            [title:'资源管理',label:'资源管理',isMenu: true,parent: 'properties_index',code: 'btnAndMenu_index'],

            [title:'新建资源',label:'新增',isMenu: false,parent: 'btnAndMenu_index',code: 'btnAndMenu_create'],
            [title:'删除资源',label:'删除',isMenu: false,parent: 'btnAndMenu_index',code: 'btnAndMenu_delete'],
            [title:'更新资源',label:'更新',isMenu: false,parent: 'btnAndMenu_index',code: 'btnAndMenu_update'],
            [title:'查看资源',label:'查看',isMenu: false,parent: 'btnAndMenu_index',code: 'btnAndMenu_detail'],

    ]
    protected Class<BtnAndMenu> persistentClass = null//(Class<T>) ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]

    protected String modelName

    private String serviceName

    ViewOperationService viewOperationService

    def service

    def exportService

    def excelImportService

    GrailsApplication grailsApplication
    def btnAndMenuService
    def roleAccessRelationService
    def authService

    public BtnAndMenuController(){
//        super(BtnAndMenu.class)
        this.modelName = "btnAndMenu"

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
            long count = btnAndMenuService.count(params)
            List<BtnAndMenu> datas = btnAndMenuService.list(params)

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

        btnAndMenuService.save( persistentClass.newInstance([paramsMap] as Object[]))

        render([success: true, message: "创建成功!"] as JSON)
    }

    /**
     * AUTO: 明细
     */
    def detailAction(){
        BtnAndMenu address = btnAndMenuService.findById(params["id"])

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
        BtnAndMenu address = btnAndMenuService.findById(params["id"])

        address.properties = viewOperationService.ConvertFromView(this.modelName, params)

        btnAndMenuService.save(address)

        String output = [success: true, message: "更新成功!"] as JSON
        render output
    }

    /**
     * AUTO：删除
     */
    def deleteAction(){
        //删除缓存
        List<Long> ids = params.data.split(",").collect { Long.parseLong(it) }
        ids.each {
            roleAccessRelationService.findByProperties([btnAndMenuId: it]).each { RoleAccessRelation rar ->
                authService.deleteCaching(rar.sysRoleId,rar.btnAndMenuId)
                roleAccessRelationService.delete(rar)
            }
        }
        //删除关联表

        ids=params.data.split(",").collect{Long.parseLong(it)}

        ids.each {
            btnAndMenuService.deleteById(it)
        }

        String output = [success: true, message: "删除成功!"] as JSON
        render output
    }

    /**
     * 获得权限树的根节点
     */
    def getRootNode() {
        BtnAndMenu btnAndMenu = btnAndMenuService.findById(1)

        String output = ['id': btnAndMenu.id, 'text': btnAndMenu.title, leaf: BtnAndMenu.findAllByParentId(btnAndMenu.id).size() == 0] as JSON
        render output
    }

    /**
     * 获取资源树信息接口
     */
    def treeList(){
        long id = Long.parseLong(params.node?:"1") //当前tree节点的ID值
        List<BtnAndMenu> nodeDatas= BtnAndMenu.findAllByParentId(id)
        List<Map> outputList=[];
        Boolean isCheck = false;
        if(params.roleId) {
            long roleId = Long.parseLong(params.roleId)

            List<Long> relations = []

            RoleAccessRelation.findAllBySysRoleId(roleId).each {RoleAccessRelation it ->
                relations.add(it.btnAndMenuId)
            }
            for(int k=0;k<nodeDatas.size();k++){
                if(relations.contains(nodeDatas[k].id)){
                    isCheck = true
                }
                outputList.add(['id':nodeDatas[k].id,'text':nodeDatas[k].title,leaf:BtnAndMenu.findAllByParentId(nodeDatas[k].id).size()==0,checked:isCheck,expanded:true])
                isCheck = false
            }
        }else{
            for(int k=0;k<nodeDatas.size();k++){
                outputList.add(['id':nodeDatas[k].id,'text':nodeDatas[k].title,leaf:BtnAndMenu.findAllByParentId(nodeDatas[k].id).size()==0,checked:isCheck,expanded:true])
            }
        }


        render outputList as JSON
    }


}