package framework

import grails.converters.JSON
import org.springframework.web.servlet.ModelAndView
/**
 * 系统统一异常处理
 * @author xfb,wuzj
 * @since 2014-08-18
 * @version 2014-08-29对系统异常的日志记录
 */
class ExceptionHandlerController {

//    def logService

    def handleException() {
        if(request.exception){
           response.status = 200
            try{
                def e = request.exception

//                String className = e.className
//                int endPosition = className.indexOf("Controller")
//                String domainName = ""
//                if(endPosition>-1){
//                    className = className.substring(0,endPosition)
//                    domainName = className.substring(0,1).toLowerCase()+className.substring(1,endPosition)
//                    domainName = ModelService.GetModel(domainName).m.domain.cn
//                }
//                String operator = session.login
//                String logMsg = e.message
//                if(logMsg.length()>255) {
//                    logMsg = logMsg.substring(0, 255)
//                }
//                Log log = new Log(
//                        catalog: "系统",
//                        operator:operator?:"无",
//                        logMsg:logMsg
//                )
//                logService.error(log)
                //ajax 请求
                if(request.getHeader("x-requested-with")){
                    String output = [success: false, message: "出错了,${e.message}！"] as JSON
                    render output
                    return
                }else {
                    return new ModelAndView('/error')
                }
            }catch (Exception exception){
                log.error('',exception)
            }
        }
        render ''
    }
}
