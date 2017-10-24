package com.nd.cube.common.cache.impl;

import com.nd.cube.common.cache.Cache;

/**
 * cache包装类,用于代理实现扩展功能
 * @xfb on 2015/1/15.
 */
public class CacheWrapper<K,V> implements Cache<K,V>{

    Cache<K,V> proxyCache;

    public CacheWrapper(Cache<K,V> cache){
        proxyCache = cache;
    }
    @Override
    public V get(K key) {
        return proxyCache.get(key);
    }

    @Override
    public void put(K key, V value) {
        proxyCache.put(key,value);
    }

    @Override
    public void put(K key, V value, long expire) {
        proxyCache.put(key,value,expire);
    }

    @Override
    public void putIfAbsent(K key, V value) {
        proxyCache.putIfAbsent(key, value);
    }

    @Override
    public void putIfAbsent(K key, V value, long expire) {
        proxyCache.put(key,value,expire);
    }

    @Override
    public boolean containsKey(K key) {
        return proxyCache.containsKey(key);
    }

    @Override
    public void remove(K key) {
        proxyCache.remove(key);
    }

    @Override
    public void invalidate(K key) {
        proxyCache.invalidate(key);
    }
}
