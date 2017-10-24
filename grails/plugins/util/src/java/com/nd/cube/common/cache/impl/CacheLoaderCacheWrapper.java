package com.nd.cube.common.cache.impl;

import com.nd.cube.common.cache.Cache;
import com.nd.cube.common.cache.CacheLoader;
import com.nd.cube.common.cache.ExpireTimes;

/**
 * cacheLoader 代理类
 * @xfb on 2015/1/16.
 */
public class CacheLoaderCacheWrapper<K,V> extends CacheWrapper<K,V> {

    private CacheLoader<K,V> cacheLoader = null;

    //过期时间秒
    private long ttl = ExpireTimes.FOREVER;

    public final CacheLoader<K,V> CACHELOAD_NULL = new CacheLoader<K,V>() {
        @Override
        public V load(Object key) {
            return null;
        }
    };

    public CacheLoaderCacheWrapper(Cache<K, V> cache, CacheLoader<K, V> cacheLoader) {
        super(cache);
        if(cacheLoader != null){
            this.cacheLoader = cacheLoader;
        }else{
            this.cacheLoader = CACHELOAD_NULL;
        }

    }


    @Override
    public V get(K key) {
        V v =  proxyCache.get(key);
        if(v != null){
            return v;
        }
        v = cacheLoader.load(key);
        this.putIfAbsent(key,v,ExpireTimes.expireTimeAfterSecond(ttl));
        return v;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
