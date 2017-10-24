package easemob
import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import yuntongxun.IMProperty

import java.util.concurrent.TimeUnit

@Transactional
class UserService {

    public static token = null;
    public static Long current;
    public String getEasemobToken(){
        if (token == null || current == null || System.currentTimeMillis() - current >= 7*24*60*60*1000){
            String url = "/token"
            def http = new HTTPBuilder(IMProperty.easemobHost+url)
            http.ignoreSSLIssues()
            http.request(Method.POST, ContentType.JSON){request->
                body = (["grant_type":"client_credentials","client_id":IMProperty.easemobClientId,"client_secret":IMProperty.easemobClientSecret] as JSON ).toString()
                response.success={resp,reader->
                    token = reader."access_token"
                    current = System.currentTimeMillis()
                }
                response.failure = { resp,json ->
                    log.error(json)
                }
            }
        }
        return token
    }


    Map bindEasymobUID(String mobile) {
        String username = "${mobile}.${UUID.randomUUID().toString()}.kanya.la"
        String password = "la.kanya.${mobile}"
        String easemobUid = null
        String accessToken = getEasemobToken()
        if (accessToken != null){
            def http = new HTTPBuilder(IMProperty.easemobHost+"/users")
            http.ignoreSSLIssues()

            http.setHeaders(["Authorization":"Bearer ${accessToken}"])
            http.request(Method.POST,ContentType.JSON){request->
                body = (["username":username,"password":password] as JSON).toString()
                response.success={resp,reader->
                    easemobUid = reader.entities[0].uuid
                }
                response.failure={resp,json->
                    log.error(json)
                }
            }
        }
        return [easemobUid:easemobUid,easemobUserName:username,easemobPassword:password]
    }
}
