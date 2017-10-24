import accessResources.BtnAndMenu
import accessResources.BtnAndMenuService
import framework.ValidateDomainService
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.commons.GrailsApplication
import relation.RoleAccessRelationService

class RbacBootStrap {

    GrailsApplication grailsApplication
    def btnAndMenuService
    def init = { servletContext ->
        if(btnAndMenuService.initRoot()){
            btnAndMenuService.initFromConfig()
            btnAndMenuService.initFromController()
        }
//        String output
//
//        if (btnAndMenuService.findById(1) == null) {
//            BtnAndMenu btnAndMenu = new BtnAndMenu()
//            btnAndMenu.code = "root"
//            btnAndMenu.isMenu = true
//            btnAndMenu.title = btnAndMenu.label = "系统资源树"
//
//            output = ValidateDomainService.validate(btnAndMenu)
//
//            boolean validateSuccess = ((LinkedHashMap) new JsonSlurper().parseText(output)).get("success")
//            if (validateSuccess) {
//                btnAndMenuService.save(btnAndMenu)
//            } else {
//                log.info("资源项初始化失败")
//            }
//        }
//
//        //从config文件中读取配置
//        List<LinkedHashMap> groupsFull = grailsApplication.config.cube.navigation.groups
//        LinkedHashMap<String, List> itemsFull = grailsApplication.config.cube.navigation.items
//        if (groupsFull != null && !groupsFull.isEmpty()) {
//                test.ResourceBuilder.btnConfigAnalyer(groupsFull)
//        }
//        LinkedHashMap<String, List> items = [:]
//
//        //对预定义所有菜单组进行循环，找出允许显示的菜单项
//        itemsFull.each { String key, List<LinkedHashMap<String, String>> value ->
//            List menuItems = []
//            for (int i = 0; i < value.size(); i++) {
//                //在不考虑缓存的情况下，这部分直接从数据库里获取parentId = 1的全部资源，并与session["roles"]做对比，匹配则存入menuItems
//                menuItems << value[i]
//            }
//
//            //对于包含多余1项的菜单组予以显示
//            if (menuItems) {
//                items[key] = menuItems
//            }
//            test.ResourceBuilder.btnConfigAnalyer(menuItems)
//
//        }
//
//
//        //读取controller中的静态配置
//        grailsApplication.controllerClasses.each { grailsClass ->
//            println grailsClass.logicalPropertyName
//            List<LinkedHashMap> btnConfig = grailsClass.getPropertyValue("buttons")
//            if (btnConfig != null && !btnConfig.isEmpty()) {
//                test.ResourceBuilder.btnConfigAnalyer(btnConfig, grailsClass.logicalPropertyName)
//            }
//        }

    }
    def destroy = {

    }
}
