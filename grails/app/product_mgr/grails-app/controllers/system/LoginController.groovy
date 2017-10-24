package system

import com.nd.grails.plugins.log.Log
import framework.util.MD5Util
import grails.converters.JSON

class LoginController {
    def logService

    def index() {}

    def login() {

        String logMsg
        String output

        if (params.login != null && params.login != "" && params.password != null && params.password != "") {
            SysAdmin sysAdmin = SysAdmin.findByLogin(params.login)

            if (sysAdmin!=null) {
                if (MD5Util.checkPassword(params.password, sysAdmin.password)) {
                    session['login'] = params.login
                    session['name'] = sysAdmin.name
//                    session["roles"] = sysAdmin.role
                    session["adminId"] = sysAdmin.id
                    if(sysAdmin.properties.containsKey("sysRoleId")){
                        //包含sysRoleId属性，则代表引入rbac插件
                        session["sysRoleId"] = sysAdmin.sysRoleId
                    }else{
                        session["sysRoleId"] = 0
                    }

                    //登录成功
                    logMsg = "${g.message(code:"default.login.success")}"
                    output = [success: true, message: "${g.message(code:"default.login.success")}"] as JSON
                } else {
                    //密码错误，直接返回
                    logMsg = "${g.message(code:"default.login.psswordError")}"
                    output = [success: false, message: "${g.message(code:"default.login.psswordError")}"] as JSON
                }
            } else {
                //根据用户名查不到对应账号，直接返回
                logMsg = "${g.message(code:"default.login.noUser")}"
                output = [success: false, message: "${g.message(code:"default.login.noUser")}"] as JSON
            }

        } else {
            //前端传入的值如果为null或者空字符串，直接返回
            logMsg = "${g.message(code:"default.login.noParams")}"
            output = [success: false, message: "${g.message(code:"default.login.noParams")}"] as JSON
        }

        //保存访客登录记录
        Log log = new Log(
                catalog: '用户',
                operator: session.login ?: '无',
                operatorName: session.name?:'未知',
                logMsg: logMsg
        )
        logService.info(log)
        render output
    }

    def logOut() {

        //用户退出保存的记录
        Log log = new Log(
                catalog:'用户',
                operator:session.login,
                logMsg:'注销'
        )
        logService.info(log)

        session['sysRoleId'] = null
        session['login'] = null
        session['orgId'] = null
        session['name'] = null
        String output = [success: true, url: request.contextPath] as JSON
        render output
    }
}
