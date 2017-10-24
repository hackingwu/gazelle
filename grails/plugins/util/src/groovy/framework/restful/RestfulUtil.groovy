package framework.restful

import grails.converters.JSON

/**
 * 工具类
 * @author xufb 2014/11/25.
 */
class RestfulUtil {

    /**
     * 出错时返回结果
     * @param msg 提示信息
     * @return
     */
    static String errorResult(String msg){

        [success:false,message:msg] as JSON
    }
}
