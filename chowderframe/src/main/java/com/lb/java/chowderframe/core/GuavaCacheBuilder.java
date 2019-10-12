package com.lb.java.chowderframe.core;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GuavaCacheBuilder<K, V> {

    Logger log = LoggerFactory.getLogger(GuavaCacheBuilder.class);

    private Cache<K, V> cache = null;


    // 缓存时间
    private int expireTime;
    // 缓存时间单位
    private TimeUnit expireTimeUnit;

    // 最大缓存数目
    private int maximumSize;
    // 缓存过期策略
    private String expireAfterType;

    private GuavaCacheBuilder(Builder builder) {
        expireTime = builder.expireTime;
        expireTimeUnit = builder.expireTimeUnit;
        maximumSize = builder.maximumSize;
        expireAfterType = builder.expireAfterType;
    }


    /**
     * 初始化cache, 单例
     * @return
     * @throws Exception
     */
    public Cache<K, V> getGuavaCache() throws Exception{
        if (cache == null) {
            if (expireAfterType == "Write") {
                cache = CacheBuilder.newBuilder().maximumSize(maximumSize)  // 缓存数目
                        .expireAfterWrite(expireTime, expireTimeUnit)  // 没有写入/覆盖操作 3秒后过期
                        .build();
            } else if (expireAfterType == "Access") {
                cache = CacheBuilder.newBuilder().maximumSize(maximumSize)  // 缓存数目
                        .expireAfterAccess(expireTime, expireTimeUnit) //没有读取操作 3秒后过期
                        .build();
            }else{
                throw new Exception("expireAfterType取值为: Write/Access");
            }
        }
        return cache;
    }


    public static class Builder {
        private int expireTime  = 60;
        private TimeUnit expireTimeUnit= TimeUnit.MINUTES;
        private int maximumSize = 1000;
        private String expireAfterType = "Write";

        public Builder maximumSize(int maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public Builder expireAfterType(String expireAfterType) {
            this.expireAfterType = expireAfterType;
            return this;
        }

        public Builder expireTimeUnit(TimeUnit expireTimeUnit) {
            this.expireTimeUnit = expireTimeUnit;
            return this;
        }

        public Builder expireTime(int expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        public GuavaCacheBuilder build() {
            return new GuavaCacheBuilder(this);
        }


        @Override
        public String toString() {
            return "Builder{" +
                    "expireTime='" + expireTime + '\'' +
                    ", expireTimeUnit='" + expireTimeUnit + '\'' +
                    '}';
        }
    }

}

