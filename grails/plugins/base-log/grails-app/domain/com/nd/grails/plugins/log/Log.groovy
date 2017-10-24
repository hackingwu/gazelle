package com.nd.grails.plugins.log
/**
 * @author :lch
 * @version :2014/8/27 增加一个用于日志搜索的objectId字段，定义日志级别
 */
class Log {

    //日志级别 1表示提示，2表示警告，3表示错误

    int level

    //日志所属应用
    int application

    //日志所属模块
    int module

    //日志分类,由应用自定义
    String catalog

    //日志记录信息
    String logMsg

    //日志发生时间
    Date birthday

    //操作者ip地址 filter中request
    String ip

    //操作人 从session中获取
    String operator

    String operatorName

    //提供给日志搜索
    String objectId

    static constraints = {
        level        attributes:[cn:'日志级别']
        application  attributes:[cn:'所属应用']
        module       attributes:[cn:'所属模块']
        operator     attributes:[cn:'操作账号']
        operatorName    attributes:[cn:'操作者名称']
        catalog      attributes:[cn:'分类']
        logMsg       attributes:[cn:'日志信息',flex:2,widget: "textareafield"]
        ip           attributes:[cn:'操作者IP地址']
        birthday     attributes:[cn:'发生时间',widget:'datetimefield']

         objectId     attributes:[cn:'搜索目标id'], nullable: true

//        catalog nullable: true
//        logMsg nullable: true
//        operator nullable: true
//        birthday nullable: true
//        ip nullable: true
//        module nullable :true
//        application nullable:true
//        level nullable:true

    }

    static m = [
            domain:[cn: "操作日志"],
            layout:[type: "standard"]
//            layout:[type: "oneToDetailDownWithRightImg"]

    ]

    static mapping = {
        logMsg type:'text'
    }

    String toString(){
        return catalog+" "+logMsg+" "+operator+" "+birthday+" "+ip+" "+module+" "+application+" "+level
    }
}
