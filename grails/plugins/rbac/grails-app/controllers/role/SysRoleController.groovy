package  role

import framework.ViewOperationService
import framework.biz.GenericService
import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.InitializingBean
import org.springframework.web.servlet.ModelAndView
import relation.RoleAccessRelation
import user.SysUser

/**
 *
 * @author lch
 * @version  2014-11-17
 */
class SysRoleController implements InitializingBean{
    static buttons = [
            //这些配在配置管理里的页面，都需要在对应的controller里去配置一个页面
            [title:'新建系统角色',label:'新增',isMenu: false,parent: 'sysRole_index',code: 'sysRole_create'],
            [title:'删除系统角色',label:'删除',isMenu: false,parent: 'sysRole_index',code: 'sysRole_delete'],
            [title:'更新系统角色',label:'更新',isMenu: false,parent: 'sysRole_index',code: 'sysRole_update'],
            [title:'查看系统角色',label:'查看',isMenu: false,parent: 'sysRole_index',code: 'sysRole_detail'],

//            [title:'查看角色权限',label:'查看角色权限',isMenu: false,parent: 'sysRole_index',code: 'loadResourceTree'],
            [title:'角色权限配置',label:'角色权限配置',isMenu: false,parent: 'sysRole_index',code: 'showResourceTree']


    ]

    protected Class<SysRole> persistentClass = null//(Class<T>) ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]

    protected String modelName

    private String serviceName

    ViewOperationService viewOperationService


    def exportService

    def excelImportService
    def sysRoleService

    GrailsApplication grailsApplication
    def authService
    def roleAccessRelationService
    def sysUserService
    public SysRoleController(){
//        super(SysRole.class)
        this.modelName = "sysRole"

    }

    /**
     * AUTO: 调用后置拦截器
     */
    def afterInterceptor = { model, modelAndView ->
        // println "afterInterceptor"
    }

    def handleException(Exception e) {

        /**
         * TODO 应用错误处理接入
         */

        log.error(e.getMessage())
        if(this.isAjaxRequest()){
            String output = [success: false, message: "出错了,${e.message}！"] as JSON
            log.error('',e)
            render output
            return
        }else {
            return new ModelAndView('/error')
        }
    }

    protected boolean isAjaxRequest(){
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"))
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
            long count = sysRoleService.count(params)
            List<SysRole> datas = sysRoleService.list(params)

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

        sysRoleService.save( persistentClass.newInstance([paramsMap] as Object[]))

        render([success: true, message: "创建成功!"] as JSON)
    }

    /**
     * AUTO: 明细
     */
    def detailAction(){
        SysRole address = sysRoleService.findById(params["id"])

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
        SysRole address = sysRoleService.findById(params["id"])

        address.properties = viewOperationService.ConvertFromView(this.modelName, params)

        sysRoleService.save(address)

        String output = [success: true, message: "更新成功!"] as JSON
        render output
    }

    /**
     * AUTO：删除
     */
    @Transactional
    def deleteAction() {
        //删除某个角色时删除对应在中间表中的记录，用户表中角色置空
        List<Long> ids = params.data.split(",").collect { Long.parseLong(it) }
        //TODO:应该限制不能删除超级管理员 后期完善 现在只有超级管理员有角色删除权限
        ids.each {
                roleAccessRelationService.findByProperties([sysRoleId: it]).each { RoleAccessRelation rar ->
                    authService.deleteCaching(rar.sysRoleId,rar.btnAndMenuId)
                    roleAccessRelationService.delete(rar)
            }
//            sysUserService.findByProperties([sysRoleId: it]).each { SysUser user ->
//                user.sysRoleId = 0
//                sysUserService.save(user)
//            }

        }

//        ids=params.data.split(",").collect{Long.parseLong(it)}

        ids.each {
            sysRoleService.deleteById(it)
        }

        String output = [success: true, message: "删除成功!"] as JSON
        render output
    }

    /**
     * AUTO: 为对象关联查询提供查询数据
     */
    def association(){
        long count = sysRoleService.count(params)
        List<SysRole> datas = sysRoleService.list(params)

        List<Map> datasToView = []

        for(int i=0;i<datas?.size();i++)
        {
            datasToView << [id: datas[i].id, text: datas[i].toString()]
        }

        String output = [success: true, message: "", totalCount: count, data: datasToView] as JSON

        render output
    }

    @Override
    void afterPropertiesSet() throws Exception {
        this.persistentClass = SysRole.class

        char[] chars = persistentClass.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()

        modelName = new String(chars)

        serviceName = "SysRoleService"

        sysRoleService = (SysRoleService)grailsApplication.mainContext.getBean("${modelName}Service")
    }
}