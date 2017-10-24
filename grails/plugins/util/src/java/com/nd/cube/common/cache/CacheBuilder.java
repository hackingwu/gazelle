package com.nd.cube.common.cache;

import com.nd.cube.common.cache.impl.CacheLoaderCacheWrapper;
import com.nd.cube.common.cache.impl.ClosureCacheLoader;
import com.nd.cube.common.cache.impl.DefaultMapCacheProvider;
import com.nd.cube.util.PropertiesWrapper;
import groovy.lang.Closure;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cache构建者
 * @author xfb on 2015/1/14.
 */
public class CacheBuilder {

    //默认cache
    private static CacheProvider defaultProvider = new DefaultMapCacheProvider();

    private static Properties defaultProperties = new Properties();

    //map 实例缓存
    private static ConcurrentHashMap<String,Cache> caches = new ConcurrentHashMap<String,Cache>();

    private static ConcurrentHashMap<String,CacheProvider> providers = new ConcurrentHashMap<String,CacheProvider>();

    static {
        init();
    }

    /**
     * cache 对应的配置
     */
    private static ConcurrentHashMap<String,Properties> properties = new ConcurrentHashMap<String,Properties>();

    private Properties currentProperties = new Properties();

    private CacheLoader cacheLoader;


    public static void registerCacheConfig(String cacheName,Properties cacheConfig){
       // cacheConfig.getProperty("");
        PropertiesWrapper pw = new PropertiesWrapper(cacheConfig);
        Class clazz = pw.getClassProperty(CacheConfig.providerClassName);
        if(clazz != null){
            try {
                CacheProvider cacheProvider = (CacheProvider)clazz.newInstance();
                providers.put(cacheName,cacheProvider);
            } catch (Exception e) {
                throw new RuntimeException("init providerClass fail.");
            }
        }else {
            throw new RuntimeException("cache config error,could't find providerClass config.");
        }
        properties.put(cacheName,cacheConfig);
    }

    /**
     * 创建cache构造器
     * @return
     */
    public static CacheBuilder newBuilder(){
        return new CacheBuilder();
    }

    /**
     * 构造一个cache,将会以class的name作为缓存的名称
     * @param clazz
     * @return
     */
    public Cache build(Class clazz){
        return build(clazz.getName());
    }

    /**
     * 构造一个cache
     * @param cacheName
     * @return
     */
    public Cache build(String cacheName){
        if(cacheName == null){
            return null;
        }
        Cache cache = caches.get(cacheName);
        if(cache != null){
            return cache;
        }

      //  currentProperties
        Properties p = properties.get(cacheName);
        if(p == null){
            p = defaultProperties;
        }

        copyProperties(p,currentProperties);

        CacheProvider cacheProvider =  providers.get(cacheName);
        if(cacheProvider == null){
            cacheProvider = defaultProvider;
        }
        cache = cacheProvider.buildCache(currentProperties);

        if(cacheLoader != null){
            CacheLoaderCacheWrapper cacheLoaderCacheWrapper= new CacheLoaderCacheWrapper(cache,cacheLoader);
            if(this.currentProperties.containsKey("ttl")){
                cacheLoaderCacheWrapper.setTtl(Long.valueOf(this.currentProperties.getProperty("ttl")));
            }
            cache = cacheLoaderCacheWrapper;
        }
        caches.put(cacheName,cache);
        return cache;
    }

    /**
     * 设置loader
     * @param cacheLoader
     */
    public CacheBuilder setCacheLoader(CacheLoader cacheLoader) {
        this.cacheLoader = cacheLoader;
        return this;
    }

    /**
     * 以闭包方式设置cacheLoader
     * @param cacheLoader
     */
    public CacheBuilder setCacheLoader(Closure cacheLoader) {
        if(cacheLoader == null){
            return this;
        }
        this.cacheLoader = new ClosureCacheLoader(cacheLoader);
        return this;
    }

    /**
     * 过期时间以秒为单位
     * @return
     */
    public CacheBuilder expire(long ttl){
        currentProperties.setProperty("ttl",String.valueOf(ttl));
        return this;
    }

//    private CacheProvider buildCacheProvider(String cacheName,Properties cacheConfig){
//
//        CacheProvider cacheProvider =  providers.get(cacheName);
//        if(cacheProvider != null){
//            return cacheProvider;
//        }
//
//        PropertiesWrapper pw = new PropertiesWrapper(cacheConfig);
//
//        Class clazz = pw.getClassProperty(CacheConfig.providerClassName);
//        if(clazz != null){
//            try {
//                cacheProvider = (CacheProvider)clazz.newInstance();
//                providers.put(cacheName,cacheProvider);
//            } catch (Exception e) {
//                throw new RuntimeException("init providerClass fail.");
//            }
//        }else {
//            throw new RuntimeException("cache config error,could't find providerClass config.");
//        }
//        return cacheProvider;
//    }



    //配置初始化
    private static void init(){
        defaultProperties.setProperty(CacheConfig.providerClassName, "com.nd.cube.common.cache.impl.DefaultMapCacheProvider");
        defaultProperties.setProperty(CacheConfig.ttlName, String.valueOf(ExpireTimes.FOREVER));
    }

    //属性copy,当target中包含时忽略
    private static void copyProperties(Properties source,Properties target){
        if(source != null && target != null){
            Iterator<Map.Entry<Object,Object>> it = source.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<Object,Object> e = it.next();
                if(!target.containsKey(e.getKey())){
                    target.put(e.getKey(),e.getValue());
                }
            }
        }
    }


}
