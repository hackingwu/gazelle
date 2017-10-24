package framework.config

/**
 * 配置服务实现
 * @author xufb 2014/10/28.
 */
public interface Configuration {

    /**
     * 根据key值获取配置属性
     * @param key
     * @return
     */
    String get(String key)
}
