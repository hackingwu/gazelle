package authority

import accessResources.BtnAndMenu
import grails.transaction.Transactional
import grails.util.Holders
import org.springframework.beans.factory.InitializingBean
import relation.RoleAccessRelation
import role.SysRole

@Transactional(readOnly = true)
class AuthorityService implements InitializingBean{
//    static transactional = false
    def grailsApplication
    def btnAndMenuService
    def roleAccessRelationService
    def sysRoleService

    static Map caching = [:]
//    List<Map> roles=[]
//    Map<String, List<String>> roleDisableMenus=[:]
//    Map roleDisableButtons=[:]
//    Map roleDisableAttributes=[:]
    /**
     * 服务初始化，将权限规则加载至内存
     */
    void afterPropertiesSet() {
//
//        roles= Holders.applicationContext.grailsApplication?.config?.cube?.auth?.roles
//        roleDisableMenus= grailsApplication?.config?.cube?.auth?.menus?.disable
//        roleDisableButtons= grailsApplication?.config?.cube?.auth?.buttons?.disable
//        roleDisableAttributes= grailsApplication?.config?.cube?.auth?.attributes?.disable
    }

    /**
     * 菜单权限
     * @param roleCode 角色代码
     * @param viewId 菜单ID
     * @return true: 有权限访问，false: 无权限访问
     */
    boolean Menu(Long roleCode, String code) {
        if (roleCode == null) {
            return false
        }
        Collection resources

        resources = caching[roleCode]
        if(resources != null){
            if(resources.contains(code)) {
                return true
            }
        }else{
            resources = new HashSet();
        }


        BtnAndMenu btnAndMenu = btnAndMenuService.findByProperties([code: code])[0]

        if (roleAccessRelationService.countByProperties([sysRoleId: Long.valueOf(roleCode), btnAndMenuId: btnAndMenu?.id]) != 0) {
            //缓存和数据库信息不统一，更新缓存
            resources.add(code)
            caching.put(roleCode, resources)
            return true
        } else {
            return false
        }
    }

    /**
     * 按钮权限
     * @param roleCode 角色代码
     * @param viewId 菜单ID
     * @param buttonId 按钮ID
     * @return true: 有权限访问，false: 无权限访问
     */
    boolean Button(Long roleCode, String code)
    {
        BtnAndMenu btnAndMenu = btnAndMenuService.findByProperties([code:code])[0]
        if(btnAndMenu == null){
            return false
        }
        String parentCode = btnAndMenuService.findById(btnAndMenu.parentId)?.code


        //无视图权限，无按钮权限
        if(!Menu(roleCode, parentCode)){
            return false
        }
        Collection resources

        resources = caching[roleCode]
        if(resources != null){
            if(resources.contains(code)) {
                return true
            }
        }else{
            resources = new HashSet();
        }

        if (roleAccessRelationService.countByProperties([sysRoleId: Long.valueOf(roleCode), btnAndMenuId: btnAndMenu?.id]) != 0) {
            //缓存和数据库信息不统一，更新缓存
            resources.add(code)
            caching.put(roleCode, resources)
            return true
        } else {
            return false
        }

    }

    static boolean ButtonStatic(Long roleCode, String code){
        BtnAndMenu btnAndMenu = BtnAndMenu.findByCode(code) ;//btnAndMenuService.findByProperties([code:code])[0]
        if(btnAndMenu == null){
            return false
        }
        String parentCode = BtnAndMenu.findById(btnAndMenu.parentId)?.code


        //无视图权限，无按钮权限
        if(!MenuStatic(roleCode, parentCode)){
            return false
        }
        Collection resources

        resources = caching[roleCode]
        if(resources != null){
            if(resources.contains(code)) {
                return true
            }
        }else{
            resources = new HashSet();
        }

        List<RoleAccessRelation> relationList  = RoleAccessRelation.executeQuery("select count(*) from RoleAccessRelation where sysRoleId=? and btnAndMenuId=?",[Long.valueOf(roleCode),btnAndMenu?.id])
        if (relationList!=null && relationList.size()>0 && relationList[0] > 0 ) {
            //缓存和数据库信息不统一，更新缓存
            resources.add(code)
            caching.put(roleCode, resources)
            return true
        } else {
            return false
        }
    }

    static boolean MenuStatic(Long roleCode, String code) {
        if (roleCode == null) {
            return false
        }
        Collection resources

        resources = caching[roleCode]
        if(resources != null){
            if(resources.contains(code)) {
                return true
            }
        }else{
            resources = new HashSet();
        }


        BtnAndMenu btnAndMenu = BtnAndMenu.findByCode(code) //.findByProperties([code: code])[0]

        List<RoleAccessRelation> relationList  = RoleAccessRelation.executeQuery("select count(*) from RoleAccessRelation where sysRoleId=? and btnAndMenuId=?",[Long.valueOf(roleCode),btnAndMenu?.id])
        if (relationList!=null && relationList.size()>0 && relationList[0] > 0) {
            //缓存和数据库信息不统一，更新缓存
            resources.add(code)
            caching.put(roleCode, resources)
            return true
        } else {
            return false
        }
    }

    /**
     * 获取导航菜单项，如果配置文件中没有，则从数据库中读取
     * @return
     */
    def getRootMenu(){
        if(Holders.applicationContext.grailsApplication.config.cube.navigation.groups){

            return  Holders.applicationContext.grailsApplication.config.cube.navigation.groups
        }else{
            //配置文件里没有，则从数据库读取
            List<Map> list = []
            btnAndMenuService.findByProperties([parentId:Long.valueOf(1)]).each {
                list.add(it.properties)
            }
            return list
        }
    }

    def getMenuLv1(){
        if(Holders.applicationContext.grailsApplication.config.cube.navigation.items){
            return Holders.applicationContext.grailsApplication.config.cube.navigation.items
        }else{
            //配置文件里没有，则从数据库读取
        }
    }

    /**
     * 根据父节点code获取对应子页
     * @return
     */
    def getItems(String code) {
        if(Holders.applicationContext.grailsApplication.config.cube.navigation.items[code]){
            return Holders.applicationContext.grailsApplication.config.cube.navigation.items[code]
        }else{
            //配置文件里没有，则从数据库读取
            List<Map> list = []
            BtnAndMenu btnAndMenu  = btnAndMenuService.findByProperties([code:code])[0]
            btnAndMenuService.findByProperties([parentId:btnAndMenu.id]).each {
                Map map = [:]
                map.putAll( it.properties)
                list.add(map)
            }

            list.each {Map it->
                if(it.link){
                    it.put("dir",it.link.toString()-it.link.toString().substring(it.link.toString().lastIndexOf("/")))
                    it.put("img",it.link.toString().split("/")[-1])
                }
            }
            return list
        }

    }
    /**
     * 系统启动时初始化权限缓存
     * @return
     */
    def initCaching() {

        sysRoleService.findAll({}).each {SysRole it ->
            Collection resources = new HashSet();
            caching.put(it.id,resources)
        }
        roleAccessRelationService.findAll({}).each {RoleAccessRelation it ->
            Long sysRoleId = it.sysRoleId
            if(caching[sysRoleId] != null){
                Collection resources = caching[sysRoleId]
                resources.add(btnAndMenuService.findById(it.btnAndMenuId).code)
                caching.put(sysRoleId,resources)
            }else{
                Collection resources = new HashSet();
                resources.add(btnAndMenuService.findById(it.btnAndMenuId).code)
                caching.put(sysRoleId,resources)
            }
        }
    }
    /**
     * 更新缓存，增加
     * @return
     */
    def addCaching(Long sysRoleId,Long resourceId) {
        String code = btnAndMenuService.findById(resourceId)?.code
        if(caching[sysRoleId] == null){
            Collection resources = new HashSet();
            resources.add(code)
            caching.put(sysRoleId,resources)
        }else{
            Collection resources = caching[sysRoleId]
            resources.add(code)
            caching.put(sysRoleId,resources)
        }


    }

    /**
     * 更新缓存，删除对应角色和缓存
     * @return
     */
    def deleteCaching(Long sysRoleId,Long resourceId) {
        String code = btnAndMenuService.findById(resourceId)?.code
        if(caching[sysRoleId] != null){
            Collection resources = caching[sysRoleId]
            resources.remove(code)
            caching.put(sysRoleId,resources)
        }

    }
    /**
     * 更新缓存，删除该资源在所有角色中的缓存
     * @return
     */
    def deleteCaching(Long resourceId) {
        String code = btnAndMenuService.findById(resourceId)?.code
        println caching
        caching.each {
            println it.key
            println it.value
            Collection set = it.value
            if(set.contains(code)){
                set.remove(code)
            }
        }

    }
    def serviceMethod() {

    }
}
