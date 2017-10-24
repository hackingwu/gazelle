package com.nd.cube.common.cache.impl;

import com.nd.cube.common.cache.Cache;
import com.nd.cube.common.cache.ExpireTimes;
import org.apache.commons.collections.map.LRUMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认以Map方式实现的cache
 * @author xfb on 2015/1/14.
 */
public class DefaultMapCache<K extends Serializable,V> implements Cache<K,V> {
    //idle
    private LRUMap cache = new LRUMap(4000);


    //过期时间,单位秒
    private long ttl = ExpireTimes.FOREVER;

    @Override
    public V get(K key) {
        CacheElement ele = (CacheElement)cache.get(key);
        if(ele != null && !ele.isInvalidate()){
            return (V)ele.getValue();
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        this.put(key,value,System.currentTimeMillis()/1000 + this.ttl);
    }

    @Override
    public void put(K key, V value, long expire) {
        cache.put(key,new CacheElement(value,expire));
    }

    @Override
    public void putIfAbsent(K key, V value) {
        if(ttl == -1L){
            this.putIfAbsent(key,value,Long.MAX_VALUE);
        }else {
            this.putIfAbsent(key,value,System.currentTimeMillis()/1000 + this.ttl);
        }
    }

    @Override
    public void putIfAbsent(K key, V value, long expire) {
        if(value != null){
            cache.put(key,new CacheElement(value,expire));
        }
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void invalidate(K key) {
        this.remove(key);
    }

    /**
     * 设置存活时间,ttl单位秒
     * @param ttl
     */
    public void setTtl(long ttl){
        this.ttl = ttl;
    }
}
