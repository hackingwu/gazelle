package filters

import com.nd.grails.plugins.log.Log
import com.nd.grails.plugins.log.LogService
import grails.converters.JSON
import model.Constants

class LogFilters {
    LogService logService

    def filters = {
        log(controller:'*',action:'update*Action|delete*Action|create*Action'){
            after={Map model->
                if (controllerName.equals("exceptionHandler") || request.getRequestURI().startsWith("/app/") || LogService.threadLocal == null) return
                Map map = ["update":"修改操作"]
                map    << ["delete":"删除操作"]
                map    << ["create":"新增操作"]
                String name = ModelService.GetModel(controllerName).m.domain.cn
                String logMsg = ""
                Set keySet = map.keySet()
                Iterator keyIterator = keySet.iterator()
                while(keyIterator.hasNext()){
                    String key = keyIterator.next()
                    if(actionName.indexOf(key)>-1){
                        logMsg = "对${name}进行${map[key]}"
                        break;
                    }
                }
                String operator = LogService.threadLocal?.get()?.get("login") ?:"guest"
                Log log = new Log(
                    module: Constants.LOG_MODULE_MGR,
                    catalog: name,
                    operator:operator,
                    logMsg:logMsg
                )
                logService.info(log)
                LogService.threadLocal.remove()
            }
        }

        appLog(uri: '/app/**'){
            after={Map model->
                if (controllerName.equals("exceptionHandler") || LogService.threadLocal == null) return
                String operator = LogService.threadLocal?.get()?.get("login")?:"guest"
                Log log = new Log(
                        module: Constants.LOG_MODULE_APP,
                        catalog: controllerName,
                        operator:operator,
                        logMsg:(String)([url:request.request.getRequestURI(),params:params,status:response.getStatus()] as JSON)
                )
                logService.info(log)
                LogService.threadLocal.remove()
            }
        }

    }
}
