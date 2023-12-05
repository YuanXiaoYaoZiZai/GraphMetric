package com.fsc.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFactory {

    private static final Logger logger = LoggerFactory.getLogger(CacheFactory.class);

    private final Map<String, BaseCache> cacheMap = new ConcurrentHashMap<>(16);

    private final static CacheFactory cacheFactory = new CacheFactory();

    private CacheFactory() {
    }

    public static CacheFactory getCacheFactory() {
        return cacheFactory;
    }

    public BaseCache getCache(String cacheName) {
        BaseCache cache = cacheMap.get(cacheName);
        if (cache == null) {
            synchronized (cacheMap) {
                cache = cacheMap.get(cacheName);
                if (cache == null) {
                    cache = buildCache(cacheName, new CacheConfig(cacheName));
                    cacheMap.put(cacheName, cache);
                    logger.info("[Cache] Create new cache [{}]. {}", cacheName, cache.getCacheConfig());
                }
            }
        }
        return cache;
    }

    private BaseCache buildCache(String cacheName, CacheConfig config) {
        if (CacheType.CAFFEINE.getCacheType().equals(config.getCacheType())) {
            return new CaffeineCache(cacheName, config);
        }
        //返回默认缓存
        return new CaffeineCache(cacheName, config);
    }

    public void printCacheSummary() {
        StringBuilder statStr = new StringBuilder("[Cache STAT] :\n");
        cacheMap.values().forEach(cache -> {
            statStr.append(getCacheStatString(cache.getCacheName(), cache.getCacheStat()));
        });
        logger.info(statStr.substring(0, statStr.length() - 1));
    }

    private String getCacheStatString(String cacheName, CacheStat stats) {
        return String.format("%30s: [hitRate:%3.2f%%] [missRate:%3.2f%%] [hitCount:%6s] [missCount:%6s] " +
                        "[reqCount:%6s] [evictionCount:%6s]\n",
                cacheName, stats.getHitRate() * 100, stats.getMissRate() * 100, stats.getHitCount(),
                stats.getMissCount(), stats.getReqCount(), stats.getEvictionCount());
    }

}
