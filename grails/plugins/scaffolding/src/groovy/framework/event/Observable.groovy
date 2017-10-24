package framework.event

/**
 * @author xufb 2014/10/29.
 */
public interface Observable {

    void addListener(Listener listener)

    void publishEvent()

}