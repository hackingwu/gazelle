package filters

import com.nd.grails.plugins.log.LogService
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class AuthFilters {
    //以后需要跳过过滤器的url都，配置在这里，key为controller名，value为一个允许通过的action队列
    static Map excludedUrl = [
            login:["login","logOut"],
            cubeMonitor:["index"],
            terminal:["login"]
    ]
    def filters = {
        all(controller: '*',action: '*',uriExclude: '/') {
            before = {
                //跳过ContentType为json的请求，即手机端的请求不走这个过滤器
                if(request.forwardURI?.contains("/app/") && request.getContentType()?.startsWith("application/json")){
                    return true
                }

                //获取ip
                String ip = request.getHeader("x-forwarded-for");
                if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                LogService.threadLocal.set([ip:ip])



                //同步超时和异步超时的问题
//                session.setMaxInactiveInterval(8); //单位为秒
                if(session["login"] != null || excludedUrl[controllerName]?.contains(actionName.toString())){
                    LogService.threadLocal.set([ip:ip,name:session["name"]?:"游客",login:session["login"]?:"guest"])
                    return true
                }else{
                    if(request.getHeader("x-requested-with")){
                        def url= grailsApplication.mainContext.getBean(ApplicationTagLib.class).createLink(controller: "login", action: "login")
                        response.addHeader("sessionstatus", "{timeout:true,url:'${url}'}")
                        return false
                    }else{
                        //http 超时处理
                        def url=request.contextPath+"/"
                        response.getWriter().write("<script text='text/javascript'>top.location.href='"+url+"';</script>")
                        return false
                    }
                }
            }

            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
