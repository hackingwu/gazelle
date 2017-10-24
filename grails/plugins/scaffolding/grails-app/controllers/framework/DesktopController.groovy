package framework

import grails.converters.JSON

/**
 * 我的桌面(我的工作台portal界面)
 * @author xufb
 * @version 2014-07-22
 */
class DesktopController {
    static buttons = [
            //这些配在配置管理里的页面，都需要在对应的controller里去配置一个页面
            [title:'桌面管理',label:'桌面管理',isMenu: true,parent: 'root',code: 'desktop_index'],

            [title:'预约数量',label:'预约数量',isMenu: false,parent: 'desktop_index',code: 'APPOINTMENT_COUNT'],
            [title:'诊疗类型预约统计',label:'诊疗类型预约统计',isMenu: false,parent: 'desktop_index',code: 'DIAGNOSE_TYPE_APPOINTMENT']
    ]
    static transactional = false

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def grailsApplication
    /**
     * 调用拦截器
     */
//    def beforeInterceptor = {
//        println "${new Date()} params:${params}"
//    }

    /**
     * 工作台首页(数据通过JSON方式加载)
     */
    def index() {

    }

    /**
     * 单个portal块获取数据
     * @returnporletData
     */
    def porletData(){

        //params["data"]是config.groovy中定义porlet的key值
        //railsApplication.config.cube.desktop.portal.porlets 以Map方式定义了portlet
        def porlet = grailsApplication.config.cube.desktop.portal.porlets.get(params["data"]);
        if(porlet != null) {
            //porlet数据是一个闭包，执行后返回具体数据
            def data = porlet.get('data')
            if(data != null){
                data = data(params,session,grailsApplication)
                render data as JSON
            }else {
                render ""
            }

        }else {
            render ""
        }
    }
}
