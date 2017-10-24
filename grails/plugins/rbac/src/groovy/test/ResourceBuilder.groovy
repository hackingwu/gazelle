package test

import accessResources.BtnAndMenu
import framework.ValidateDomainService
import groovy.json.JsonSlurper
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Created by Administrator on 2014/11/12.
 * @Auth :lch
 * @version :2014/11/12
 */
class ResourceBuilder {
    static Log log = LogFactory.getLog(ResourceBuilder.class)

    static Boolean btnConfigAnalyer(List<LinkedHashMap> list, String controllerName = null){
        String output
        try{
            list.each {Map it->
                if(BtnAndMenu.findByCode(it.code) != null){
                    //如果资源标示已经存在则不做处理直接返回
                    return
                }
                BtnAndMenu btnAndMenu = new BtnAndMenu()
                if(it.parent == null && it.isMenu == false){
                    //父节点不存在并且还不是菜单，为异常情况
                    btnAndMenu.parentId = BtnAndMenu.findByCode(controllerName)?.id
                }else{
                    if(it.parent != null){
                        if(BtnAndMenu.findByCode(it.parent.toString()) != null){
                            btnAndMenu.parentId = BtnAndMenu.findByCode(it.parent.toString())?.id
                        }else{
                            btnAndMenu.parentId = 1
                        }
                    }
                    if(it.isMenu != null){
                        btnAndMenu.isMenu = it.isMenu
                    }
                }

                btnAndMenu.title = it.title
                btnAndMenu.label = it.label?:it.title
                btnAndMenu.code = it.code

                btnAndMenu.action = it.action
                btnAndMenu.controller = it.controller?:controllerName
                btnAndMenu.link = it.link?:it?.dir+"/"+it?.img
                btnAndMenu.click = it.click
                if(it.order != null) {
                    btnAndMenu.showOrder = Integer.parseInt(it.order)
                }


                output = ValidateDomainService.validate(btnAndMenu)

                boolean validateSuccess = ((Map)new JsonSlurper().parseText(output)).get("success")
                if (validateSuccess){
                    btnAndMenu.save()
                }else{
                    log.info("${it}资源项初始化失败")
                    return false
                }
            }
        }catch (Exception e){
            e.printStackTrace()
            log.info(e.getMessage())
            return false

        }
        return true

    }
}
