package framework.event

/**
 * 基本事件
 * @author xufb 2014/10/28.
 */
abstract class AbstractEvent implements Serializable{

    private static final long serialVersionUID = 5516076349610653487L

    //事件名称,Listener可根据事件名称订阅事件
    String name

    //事件源
    protected  Object  source

    public Object getSource() {
        return source;
    }

//    public Object getSource() {
//        return source;
//    }

}
