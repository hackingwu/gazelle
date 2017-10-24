package system

import exception.BizException

class ExceptionHandlerController {

    def index() {

        if (request.exception != null){
            if (request.exception.cause instanceof BizException){
                BizException exception = (BizException)request.exception.cause
                response.status = exception.getStatus().value()
                render exception.errorMessage()
            }
        }

    }
}
