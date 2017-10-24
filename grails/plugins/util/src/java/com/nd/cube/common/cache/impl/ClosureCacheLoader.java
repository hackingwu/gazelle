package com.nd.cube.common.cache.impl;

import com.nd.cube.common.cache.CacheLoader;
import groovy.lang.Closure;

/**
 *
 * xufb on 2015/2/2.
 */
public class ClosureCacheLoader implements CacheLoader {

    private  Closure loader = null;

    public ClosureCacheLoader(Closure cacheLoader){
        this.loader = cacheLoader;
    }
    @Override
    public Object load(Object key) {
        return loader.call(key);
    }
}
