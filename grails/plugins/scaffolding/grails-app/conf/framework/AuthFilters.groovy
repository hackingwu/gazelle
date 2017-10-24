package framework

/**
 * 全局权限拦截器
 * @author bruce.lin.chn
 * @version 2014-06-18 新做成
 */
class AuthFilters {


    def filters = {
        index(uri: "/index") {
            before = {

//                if(session["login"]==null)
//                {
//                    redirect(controller: "auth", action: "index")
//                    return false
//                }

                return true
            }
        }

        all(controller:'*', action:'*') {
            before = {
                return true
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
