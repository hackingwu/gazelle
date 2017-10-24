package framework.exception

/**
 * 前端提示性异常.
 * 提示用户信息
 * @author xufb 2014/10/28.
 */
class FrontPromptException extends RuntimeException{

    String keyCode

    String message

    FrontPromptException(String keyCode){
        super(keyCode)
    }

    FrontPromptException(String keyCode,Throwable cause){
        super(keyCode,cause)
    }

}
