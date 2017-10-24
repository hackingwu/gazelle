package exception

import framework.exception.RuntimeError
import grails.converters.JSON
import org.springframework.http.HttpStatus

/**
 * Created by Zhijian on 2015/7/4.
 */
class BizException extends RuntimeError{

    private HttpStatus status;

    private String code;

    BizException(String message){
        this(message,HttpStatus.INTERNAL_SERVER_ERROR,"INTERVAL_SERVER_ERROR")
    }

    BizException(String message, HttpStatus status, String code) {
        super(message)
        this.status = status
        this.code = code
    }

    JSON errorMessage(){
        return ["code" : code,
                "message" : message,
                "createdAt" : System.currentTimeMillis()] as JSON
    }

    HttpStatus getStatus() {
        return status
    }
}
