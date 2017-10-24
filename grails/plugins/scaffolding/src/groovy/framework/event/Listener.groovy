package framework.event

/**
 * 系统事件监听者
 * @author xufb 2014/10/29.
 */
public interface Listener {

    /**
     * listener 对发生的事件进行处理
     * @param event
     */
    void doEvent(AbstractEvent event)

    /**
     * 是否支持当前事件
     * @param event
     * @return
     */
    boolean support(AbstractEvent event)

}