package framework.exception

/**
 * 可以提示到前端的异常.
 * 提示用户信息,如注册时用户名已存在,删除组织下存在用户
 * @author xufb 2014/10/28.
 */
class BusinessException extends FrontPromptException {

    BusinessException(String keyCode) {
        super(keyCode)
    }

    BusinessException(String keyCode, Throwable cause) {
        super(keyCode, cause)
    }
}
