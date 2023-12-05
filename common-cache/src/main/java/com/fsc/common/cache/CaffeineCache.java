package com.fsc.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CaffeineCache implements BaseCache {

    private final Cache<String, Object> cacheProxied;

    private final String cacheName;
    private final CacheConfig cacheConfig;

    public CaffeineCache(String cacheName, CacheConfig cacheConfig) {
        this.cacheProxied = Caffeine.newBuilder().maximumSize(cacheConfig.getCacheMaximumSize())
                .expireAfterWrite(cacheConfig.getCacheExpireSecondAfterWrite(), TimeUnit.SECONDS)
                .expireAfterAccess(cacheConfig.getCacheExpireSecondAfterAccess(), TimeUnit.SECONDS)
                .recordStats().build();
        this.cacheName = cacheName;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Object getCacheProxied() {
        return cacheProxied;
    }

    @Override
    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public Object get(String key) {
        return cacheProxied.getIfPresent(key);
    }

    @Override
    public Map<String, Object> getMaps() {
        return cacheProxied.asMap();
    }

    @Override
    public void put(String key, Object value) {
        cacheProxied.put(key, value);
    }

    @Override
    public void remove(String key) {
        cacheProxied.invalidate(key);
    }

    @Override
    public void removeAll() {
        cacheProxied.invalidateAll();
    }

    @Override
    public CacheStat getCacheStat() {
        CacheStats stats = cacheProxied.stats();
        CacheStat cacheStat = new CacheStat();
        cacheStat.setReqCount(stats.requestCount());
        cacheStat.setHitCount(stats.hitCount());
        cacheStat.setHitRate(stats.hitRate());
        cacheStat.setMissCount(stats.missCount());
        cacheStat.setMissRate(stats.missRate());
        cacheStat.setTotalLoadTime(stats.totalLoadTime());
        cacheStat.setLoadCount(stats.loadCount());
        cacheStat.setLoadSuccessCount(stats.loadSuccessCount());
        cacheStat.setLoadFailureCount(stats.loadFailureCount());
        cacheStat.setEvictionCount(stats.evictionCount());
        cacheStat.setEvictionWeight(stats.evictionWeight());
        return cacheStat;
    }

}
