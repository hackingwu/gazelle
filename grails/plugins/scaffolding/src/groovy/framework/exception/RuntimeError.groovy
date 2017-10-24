package framework.exception

/**
 *  系统错误.
 *  模块或系统出现严重错误,如数据库连接不上、短信服务不能发送;此类错误需捕获并通知到管理员
 *  @author xufb 2014/10/28.
 */
class RuntimeError extends RuntimeException{

    public RuntimeError() {
        super();
    }


    public RuntimeError(String message) {
        super(message);
    }

    public RuntimeError(String message, Throwable cause) {
        super(message, cause);
    }


    public RuntimeError(Throwable cause) {
        super(cause);
    }

}
