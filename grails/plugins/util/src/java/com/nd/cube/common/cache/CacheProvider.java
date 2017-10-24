package com.nd.cube.common.cache;

import java.util.Properties;

/**
 * cache 缓存提供,创建具体缓存实现
 * @author xfb on 2015/1/14.
 */
public interface CacheProvider {

    /**
     * 构造一个Cache
     * @param properties cache配置
     * @return
     */
    Cache buildCache(Properties properties);
    
}
