package com.fsc.common.cache;

import java.util.Map;

public interface BaseCache {

    public Object getCacheProxied();

    public String getCacheName();

    public CacheConfig getCacheConfig();

    public CacheStat getCacheStat();

    public Object get(String key);

    public Map<String, Object> getMaps();

    public void put(String key, Object value);

    public void remove(String key);

    public void removeAll();

}
