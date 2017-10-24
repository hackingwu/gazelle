package com.nd.cube.common.cache;

/**
 * 缓存接口
 * @author xfb on 2015/1/12.
 */
public interface Cache<K ,V> {

    /**
     * 获取缓存中的值
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 向缓存中设置值
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * 向缓存中设置值
     * @param key
     * @param value
     * @param expire 过期时间，unix time 时间戳,以秒为单位
     */
    void put(K key, V value,long expire);

    /**
     * 向缓存中设置值,if(value == null){忽略}
     * @param key
     * @param value
     */
    void putIfAbsent(K key,V value);

    /**
     * 向缓存中设置值,if(value == null){忽略}
     * @param key
     * @param value
     * @param expire 过期时间，unix time 时间戳,以秒为单位
     */
    void putIfAbsent(K key,V value,long expire);

    /**
     * 是否包含某人key值
     * @param key
     * @return
     */
    boolean containsKey(K key);

    /**
     * 移除一个对象
     * @param key
     */
    void remove(K key);

    /**
     * 让key对应值,缓存失效
     * @param key
     */
    void invalidate(K key);

}
