package framework.event

/**
 * @author xufb 2014/10/29.
 */
class ListenerFactory implements Listener {

    List<Listener> listeners = []

    @Override
    void doEvent(AbstractEvent event) {
        listeners.each {
            if(it.support(event)){
                it.doEvent(event)
            }
        }
    }

    @Override
    boolean support(AbstractEvent event) {
        return true
    }

    void addListener(Listener listener){
        listeners.add(listener)
    }
}
