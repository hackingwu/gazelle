package framework.util;

import framework.config.ConfigProvider;
import framework.config.Configuration;
import framework.config.impl.DefaultConfigProvider;
import framework.config.impl.MultiConfiguration;
import grails.util.Environment;
import groovy.lang.GroovyClassLoader;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.springframework.beans.BeanUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 读取property中配置文件
 * @author xfb
 * @since  2014/8/22
 */
public abstract class PropertyConfig {

    private static Map<String,String> properties;

    private static Map<Environment,Map> environmentProperties = new HashMap<Environment,Map>();

    //配置文件
    private static String CONF_FILE = "config.properties";

    //配置文件
    private static Map<Environment,String> CONF_FILES = new HashMap<Environment,String>();

    private static Configuration configuration = null;

    static {
        //初始化  configuration
        init();
    }


    /**
     * 获取propertyName属性值
     * @param propertyName
     * @return
     */
    public static String getProperty(String propertyName){
       return configuration.get(propertyName);
    }

    /**
     * 获取propertyName属性值
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public static String getProperty(String propertyName,String defaultValue){
        String r = getProperty(propertyName);

        return r != null ? r : defaultValue;
    }

    /**
     * 获取的值转为Int
     * @param propertyName
     * @return
     */
    public static int getInt(String propertyName){
       return Integer.valueOf(getProperty(propertyName));
    }

    /**
     * 获取的值转为Int
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public static int getInt(String propertyName,int defaultValue){
        String r = getProperty(propertyName);
        return r != null ? Integer.valueOf(r) : defaultValue;
    }

    /**
     * 获取的值转为Boolean
     * @param propertyName
     * @return
     */
    public static boolean getBoolean(String propertyName){
        return Boolean.valueOf(getProperty(propertyName));
    }

    /**
     * 获取的值转为Boolean
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String propertyName,Boolean defaultValue){
        String r = getProperty(propertyName);
        return r != null ? Boolean.valueOf(r) : defaultValue;
    }

    /**
     * 初始化数据
     */
    private static void init(){
        GroovyClassLoader classLoader = new GroovyClassLoader(PropertyConfig.class.getClassLoader());
        final String configName = "ConfigurationConfig";
        URL url = classLoader.getResource(configName + ".class");

        ConfigObject config = new ConfigObject();

        if(url != null){
            try {
                config = new ConfigSlurper().parse(classLoader.loadClass(configName));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<Map> providers = null;
        boolean  defaultProvider = true;
        if(config != null){
            if(config.get("cube") != null && ((Map)config.get("cube")).get("config") != null){
                Object obj = ((Map)config.get("cube")).get("config");
                if(obj instanceof Map){
                    providers = (List)((Map)obj).get("providers");
                    Object dp = ((Map)obj).get("defaultProvider");
                    if(dp != null){
                        if(dp instanceof String){
                            defaultProvider = Boolean.valueOf((String)dp);
                        }else if( dp instanceof Boolean){
                            defaultProvider = (Boolean)dp;
                        }
                    }
                }

            }
        }

        Map properties = new HashMap();
        Configuration defaultConfiguration =  new DefaultConfigProvider().buildConfiguration(properties);
        configuration = defaultConfiguration;
        if(providers != null && providers.size()>1) {
            MultiConfiguration  cfg = new MultiConfiguration();
            if(defaultProvider){
                cfg.addConfiguration(defaultConfiguration);
            }

            for(Map m : providers){
                cfg.addConfiguration(buildConfiguration(m));
            }

            configuration = cfg;
        }
    }

    //构造config
    private static Configuration buildConfiguration(Map map){
        Configuration cfg = null;
        if(map.containsKey("provider")){
            Object p =  map.get("provider");
            ConfigProvider provider = null;
            if(p instanceof Class){
                provider = (ConfigProvider) BeanUtils.instantiate((Class) p);
            }else {
                GroovyClassLoader classLoader = new GroovyClassLoader(PropertyConfig.class.getClassLoader());
                Class cls = null;
                try {
                    cls = classLoader.loadClass(String.valueOf(p));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                provider = (ConfigProvider) BeanUtils.instantiate(cls);
            }
            cfg = provider.buildConfiguration(map);
        }
        return cfg;
    }

}
