package com.fsc.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    public CacheConfig getConfig(String cacheName) {
        try {
            return CacheFactory.getCacheFactory().getCache(cacheName).getCacheConfig();
        } catch (Exception e) {
            logger.error("[Cache] CacheCfg Get failed. [{}]", cacheName, e);
        }
        return null;
    }


    public Object get(String cacheName, String key) {
        try {
            return CacheFactory.getCacheFactory().getCache(cacheName).get(key);
        } catch (Exception e) {
            logger.error("[Cache] Cache Get failed. [{}] [{}]", cacheName, key, e);
        }
        return null;
    }

    public Map<String, Object> getMaps(String cacheName) {
        try {
            return CacheFactory.getCacheFactory().getCache(cacheName).getMaps();
        } catch (Exception e) {
            logger.error("[Cache] Cache Get Maps failed. [{}]", cacheName, e);
        }
        return null;
    }

    public void put(String cacheName, String key, Object value) {
        try {
            CacheFactory.getCacheFactory().getCache(cacheName).put(key, value);
        } catch (Exception e) {
            logger.error("[Cache] Cache Push failed. [{}] [{}] [{}]", cacheName, key, value, e);
        }
    }

    public void remove(String cacheName, String key) {
        try {
            CacheFactory.getCacheFactory().getCache(cacheName).remove(key);
        } catch (Exception e) {
            logger.error("[Cache] Cache Remove failed. [{}] [{}]", cacheName, key, e);
        }
    }

    public void removeAll(String cacheName) {
        try {
            CacheFactory.getCacheFactory().getCache(cacheName).removeAll();
        } catch (Exception e) {
            logger.error("[Cache] Cache RemoveAll failed. [{}]", cacheName, e);
        }
    }

}
