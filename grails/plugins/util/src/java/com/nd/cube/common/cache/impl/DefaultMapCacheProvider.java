package com.nd.cube.common.cache.impl;

import com.nd.cube.common.cache.Cache;
import com.nd.cube.common.cache.CacheProvider;

import java.util.Properties;

/**
 * MapCache构造者
 * @author  on 2015/1/14.
 */
public class DefaultMapCacheProvider implements CacheProvider{

    @Override
    public Cache buildCache(Properties properties) {
        return new DefaultMapCache();
    }
}
