package framework.event

/**
 *
 *  @author xufb 2014/10/29.
 */
abstract class AbstractListener implements Listener {

    @Override
    boolean support(AbstractEvent event) {

        return event == getEventName()
    }

    abstract getEventName()
}
