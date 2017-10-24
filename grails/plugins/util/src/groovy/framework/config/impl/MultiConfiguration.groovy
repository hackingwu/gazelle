package framework.config.impl

import framework.config.Configuration

/**
 * @author xufb 2014/10/28.
 */
class MultiConfiguration  implements Configuration{

    List<Configuration> cfgs = []

    public MultiConfiguration(){

    }

    public MultiConfiguration(Configuration cfg){
        this.addConfiguration(cfg)
    }
    @Override
    String get(String key){
        Iterator<Configuration> it = cfgs.iterator()
        String str = null
        while (it.hasNext() && str == null){
            str = it.next().get(key)
        }
        return str
    }

    void addConfiguration(Configuration cfg){
        if(cfg != null){
            cfgs << cfg
        }
    }
}
