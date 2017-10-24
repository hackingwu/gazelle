package framework.config.impl

import framework.config.Configuration

/**
 * @author xufb 2014/10/28.
 */
class DefaultConfiguration implements Configuration{

    private  final Map<String,String> properties

    public DefaultConfiguration(Map<String,String> properties){
        if(properties == null){
            properties = [:]
        }
        this.properties = properties
    }

    @Override
    String get(String key){
       this.properties.get(key)
    }

}
