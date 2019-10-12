package com.lb.java.chowderframe.utils;

import com.google.common.cache.Cache;
import com.lb.java.chowderframe.core.GuavaCacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCacheUtils<K, V> {

    static Logger log = LoggerFactory.getLogger(GuavaCacheUtils.class);
    private  GuavaCacheBuilder<K, V> cacheBuilder;
    private  Cache<K, V> cache;
    ZookeeperUtils zku = new ZookeeperUtils();



    public GuavaCacheUtils() throws Exception{
        cacheBuilder = new GuavaCacheBuilder.Builder().expireTime(14).expireTimeUnit(TimeUnit.SECONDS).build();
        cache = cacheBuilder.getGuavaCache();
    }

    /**
     * 从缓存中得到数据
     * @param key
     * @return
     */
    public   V getCallableCache(K key) {
        try {
            //Callable只有在缓存值不存在时，才会调用
            return cache.get(key, new Callable<V>() {
                @Override
                public V call() throws Exception {
                    // 模拟从数据库取数逻辑
                    log.info("被动更新缓存, key:" + key);
                    return zku.findZkPath(key.toString());
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新缓存
     * @param key
     * @param value
     */
    public  void putCallableCache(K key, V value) {
        log.info("主动更新缓存, key:" + key.toString() + ", value:" + value.toString());
        cache.put(key, value);
    }

    /**
     * 删除缓存
     * @param key
     */
    public void  deleteCallableCache(K key){
        log.info("删除缓存, key:" + key.toString());
        cache.invalidate(key);
    }
}

