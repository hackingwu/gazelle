package framework.config

/**
 * 配置项提供者
 *  @author xufb 2014/10/28.
 */
public interface ConfigProvider {

    /**
     *
     cube.config {
            defaultProvider = true
            providers = [
                     ['provider':ConfigProvider.class,'configFile':'dd'],
                     ['provider':Provider.class,'configFile':'dd']
            ]
     }
     * 构造Config对象需要的参数
     * @param args 除 cube.config.providers[0] 里出
     * @return
     */
    Configuration buildConfiguration(Map args)

}