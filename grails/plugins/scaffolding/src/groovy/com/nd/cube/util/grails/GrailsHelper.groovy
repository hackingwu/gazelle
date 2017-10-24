package com.nd.cube.util.grails

import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * Created by Administrator on 2015/1/8.
 */
class GrailsHelper {

    /**
     * 在非Service、Controller层获取application
     * @return
     */
    static GrailsApplication  getGrailsApplication(){
        Holders.grailsApplication
    }

    /**
     * 获取ApplicationTagLib,调用tagLib中内部方法,如:g.createLink(controller: constraint.domain, action: constraint.action)
     * @return
     */
    static ApplicationTagLib getApplicationTagLib(){
        getGrailsApplication().mainContext.getBean(ApplicationTagLib.class)
    }

    /**
     * 创建链接
     * @param attrs
     * @return
     */
    static String createLink(Map attrs){
        getApplicationTagLib().createLink(attrs)
    }

    static ValidationTagLib getValidationTagLib(){
        getGrailsApplication().mainContext.getBean(ValidationTagLib.class)
    }

    /**
     * 国际化消息
     * @param attrs[code:'',args:[]]
     * @return
     */
    static String message(Map attrs){
        getValidationTagLib().message(attrs)
    }

    /**
     * 判断某个类类型是否为domain
     * @param clazz
     * @return
     */
    static boolean isDomain(Class clazz){
        getGrailsDomainClass(clazz)? true : false
    }

    /**
     * 获取domain的GrailsDomain的封装类
     * @param clazz
     * @return
     */
    static GrailsDomainClass getGrailsDomainClass(Class clazz){
        getGrailsApplication().getDomainClass(clazz?.name)
    }

}
