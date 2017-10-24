package framework

import grails.util.Holders
import org.springframework.beans.factory.InitializingBean

/**
 * 权限相关服务，处理：菜单、按钮以及数据权限
 * @author :lch
 * @version 2014/9/2 修改权限服务，对那些没有角色信息的登陆用户，屏蔽所有页面
 */
class AuthService implements InitializingBean{

    static transactional = false
    def grailsApplication

    List<Map> roles=[]
    Map<String, List<String>> roleDisableMenus=[:]
    Map roleDisableButtons=[:]
    Map roleDisableAttributes=[:]
    /**
     * 服务初始化，将权限规则加载至内存
     */
    void afterPropertiesSet() {
        roles= grailsApplication?.config?.cube?.auth?.roles
        roleDisableMenus= grailsApplication?.config?.cube?.auth?.menus?.disable
        roleDisableButtons= grailsApplication?.config?.cube?.auth?.buttons?.disable
        roleDisableAttributes= grailsApplication?.config?.cube?.auth?.attributes?.disable
    }

    /**
     * 菜单权限
     * @param roleCode 角色代码
     * @param viewId 菜单ID
     * @return true: 有权限访问，false: 无权限访问
     */
    boolean Menu(String roleCode, String viewId)
    {
        if(roleCode == null){
            return false
        }
        //黑名单中发现，无权限
        if(roleDisableMenus[roleCode] && roleDisableMenus[roleCode]?.contains(viewId)==true)
        {
            return false
        }else {
            return true
        }
    }

    /**
     * 按钮权限
     * @param roleCode 角色代码
     * @param viewId 菜单ID
     * @param buttonId 按钮ID
     * @return true: 有权限访问，false: 无权限访问
     */
    boolean Button(String roleCode, String viewId, String buttonId)
    {
        //无视图权限，无按钮权限
        if(Menu(roleCode, viewId)==false){
            return false
        }

        Map<String,List<String>> disableButtonMap=roleDisableButtons[roleCode]

        //黑名单中发现，无权限
        if(disableButtonMap && disableButtonMap[viewId]?.contains(buttonId)==true)
        {
            return false
        }else {
            return true
        }
    }

    /**
     * 可访问属性清单
     * @param roleCode 角色代码
     * @param domain 模型，类似: programmer, student
     * @param viewId 菜单ID
     * @return
     */
    List<String> DisableAttributes(String roleCode, String domain, String viewId=null)
    {
        Map<String,List<String>> disableAttributesMap=roleDisableAttributes[roleCode]

        String key = "${domain}${viewId==null?'':"_${viewId}"}"

        if(disableAttributesMap[key])
        {
//            //防御性代码：在Test以及Development模式下检查配置属性的名称
//            if(grails.util.Environment.current.name !=grails.util.Environment.PRODUCTION.name)
//            {
//                Map model=ModelService.GetModel(domain)
//
//                println model
//
//                if(model)
//                {
//                    List<String> fields = model?.fields*.name
//
//                    for(int i=0;i<disableAttributesMap[key].size();i++){
//                        if(fields.contains(disableAttributesMap[key][i])==false){
//                            log.error("权限属性中的${roleCode}-${domain}-${disableAttributesMap[key][i]}名称配置异常")
//                        }
//                    }
//                }else{
//                    log.error("权限属性中的${roleCode}-${domain}名称配置异常")
//                }
//            }

            return disableAttributesMap[key]
        }else{
            return []
        }
    }


    /**
     * 菜单权限
     * @param roleCode 角色代码
     * @param viewId 菜单ID
     * @return true: 有权限访问，false: 无权限访问
     */
    boolean Menu(Long roleCode, String code)
    {
        return true
    }

    /**
     * 按钮权限
     * @param roleCode 角色代码
     * @param code 菜单ID
     * @return true: 有权限访问，false: 无权限访问
     */
    boolean Button(Long roleCode, String code)
    {
        println("********test button")
        return true
    }

    /**
     * 获取导航菜单项，如果配置文件中没有，则从数据库中读取
     * @return
     */
    def getRootMenu(){

        return  Holders.applicationContext.grailsApplication.config.cube.navigation.groups

    }

    /**
     * 根据父节点code获取对应子页
     * @return
     */
    def getItems(String code) {

        return Holders.applicationContext.grailsApplication.config.cube.navigation.items[code]

    }

    /**
     * 系统启动时初始化权限缓存
     * @return
     */
    def initCaching() {

    }

    /**
     * 更新缓存，增加
     * @return
     */
    def addCaching(Long sysRoleId,Long resourceId) {


    }
    /**
     * 更新缓存，删除对应角色和缓存
     * @return
     */
    def deleteCaching(Long sysRoleId,Long resourceId) {


    }

    /**
     * 更新缓存，删除该资源在所有角色中的缓存
     * @return
     */
    def deleteCaching(Long resourceId) {

    }
}