package framework.config.impl

import framework.config.ConfigProvider
import framework.config.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils

/**
 * @author xufb 2014/10/28.
 */
class DefaultConfigProvider implements ConfigProvider{

    private static String CONF_FILE = 'config.properties'

    private static String CONF_FILE_NAME = 'configFile'
    @Override
    Configuration buildConfiguration(Map args) {
        String filePath = CONF_FILE
        if(args.containsKey(CONF_FILE_NAME)){
            filePath = args.get(CONF_FILE_NAME)
        }

        Map<String,String> properties = [:]

        ClassPathResource resource =  new ClassPathResource(filePath)
        if(resource.exists()){
            fillMap(properties,PropertiesLoaderUtils.loadProperties(resource))
        }

        return new DefaultConfiguration(properties)
    }

    /**
     * 将pro中值复制至map中
     * @param map
     * @param pro
     */
    private static void fillMap(Map map,Properties pro){
        if(map != null && pro != null){
            pro.each { String key,String value ->
                map.put(key,value)
            }
        }
    }

    /**
     * 初始化数据
     */
//    private static void load(){
//        if(properties == null){
//            properties = new HashMap<>()
//
//            CONF_FILES.clear()
//            environmentProperties.clear()
//
//            CONF_FILES.put(Environment.DEVELOPMENT,'config_dev.properties')
//            CONF_FILES.put(Environment.PRODUCTION,'config_pro.properties')
//            CONF_FILES.put(Environment.TEST,'config_test.properties')
//
//            ClassPathResource resource =  new ClassPathResource(CONF_FILE)
//            if(resource.exists()){
//                fillMap(properties,PropertiesLoaderUtils.loadProperties(resource))
//            }
//
//
//            CONF_FILES.each {Environment env,String value ->
//                Map m = new HashMap()
//
//                ClassPathResource r = resource = new ClassPathResource(value)
//
//                if(r.exists()){
//                    fillMap(m,PropertiesLoaderUtils.loadProperties(r))
//                    environmentProperties.put(env,m)
//                }
//
//
//
//            }
//            //设置当前环境值
//            Map env = environmentProperties.get(Environment.getCurrent())
//
//            if(env != null){
//                properties = env
//            }
//
//        }
//    }
}
