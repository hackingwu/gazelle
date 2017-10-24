package com.nd.cube.common.cache;

/**
 * 更据
 * @xfb on 2015/1/15.
 */
public interface CacheLoader<K,V> {

    V load(K key);
}
