

/**
 * Created by hackingwu on 2017/10/20.
 */

//----------------------------------------------
// 系统主页相关配置
//----------------------------------------------
grails.app.title = "生产管理系统"
grails.app.subtitle = ""
grails.app.footer = "版权所有 © Gazelle 2014-${new Date().year+1900}"
grails.app.logo = "../images/logo.png"

//----------------------------------------------
// 导航菜单相关配置
//----------------------------------------------
cube.navigation.home=[title:"系统首页",controller:"desktop",action: "index"] //系统首页

cube.navigation.groups=[
    [code: "businessMng", label: "业务管理",title:"业务管理",isMenu:true,parent:"root"],
    [code: "operationsMng", label: "运营管理",title:"运营管理",isMenu:true,parent:"root"],
    [code: "reportMng", label: "报表管理",title:"报表管理",isMenu:true,parent:"root"],
    [code: "configurationMng", label: "配置管理",title:"配置管理",isMenu:true,parent:"root"],
    [code: "systemMng", label: "系统管理",title:"系统管理",isMenu:true,parent:"root"]
]

cube.navigation.items=[
        businessMng:[
//            [code: "appointment_index",controller: "appointment",action:"index",label: "预约管理",dir:"/images/menu",img:"appointment.png",title:"预约管理",isMenu:true,parent:"businessMng"]//,
        ],
        operationsMng:[
                [controller:"formView",action: "index", label: "基本form表单", dir: "/images/menu", img: "importInfo.png", plugin: "scaffolding",code:"formView_index",isMenu: true,parent:"operationsMng",title:"基本form表单"],
//            [code: "customer_index",controller:"customer",action: "index", label: "客户管理", dir: "/images/menu", img: "customer.png",title:"客户管理",isMenu:true,parent:"operationsMng"],
            [code: "importInfo_index",controller: "importInfo",action:"index",label: "导入导出管理",dir:"/images/menu",img:"importInfo.png",title:"导入导出管理",isMenu:true,parent:"operationsMng"]
        ],
        reportMng:[
//            [code: "report_couponsConsumptionReport",controller:"report",action: "couponsConsumptionReport", label: "卡券消费统计", dir: "/images/menu", img: "couponsConsumptionReport.png",title:"卡券消费统计",isMenu:true,parent:"reportMng"]
        ],
        configurationMng:[
            [code: "messageTemplate_index",controller:"messageTemplate",action: "index", label: "短信模板配置", dir: "/images/menu", img: "semiFinishedProductIn.png",title:"短信模板管理",isMenu:true,parent:"configurationMng"],
        ],
        systemMng:[
            [code: "sysAdmin_index",controller:"sysAdmin",action: "index", label: "用户管理", dir: "/images/menu", img: "sysAdmin.png",title:"用户管理",isMenu:true,parent:"systemMng"],
            [code: "sysRole_index",controller:"sysRole",action: "index", label: "角色管理", dir: "/images/menu", img: "sysRole.png",title:"角色管理",isMenu:true,parent:"systemMng"],
            [code: "log_index",controller:"log",action: "index", label: "系统日志管理", dir: "/images/menu", img: "log.png",title:"日志管理",isMenu:true,parent:"systemMng"]
        ]
]

//----------------------------------------------
// 权限相关配置
//----------------------------------------------

//系统角色
cube.auth.roles=[
    [code: "ROLE_ADMIN", name: "管理员"],
    [code: "ROLE_USER", name: "用户"]
]
//角色菜单(采用黑名单方式)
cube.auth.menus.disable=[
    ROLE_ADMIN: [],
    ROLE_USER:  ["sysAdmin_index","sysRole_index"]
]

//角色按钮(采用黑名单方式)
cube.auth.buttons.disable=[
    ROLE_ADMIN: [:
    ],
    ROLE_USER: [
            cppMembrane_index:["create","update","delete"]
    ]
]

//----------------------------------------------
// 桌面菜单相关配置
//----------------------------------------------
cube.desktop.portal = [
        //portal 权限配置
        auths:['ROLE_ADMIN':[],
               'ROLE_USER' :[]
        ],

        columns :2,
        rows :1,
        width:1024,
        height:512,
        dataUrl : "desktop/porletData",

        orders : [

        ],

        porlets : [
            :
        ]
]

