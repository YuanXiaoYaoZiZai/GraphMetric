package com.fsc.common.cache;

import com.fsc.common.config.ConfigTools3;

public class CacheConfig {

    public static final String CACHE_TYPE = "cache.type";

    public static final String CACHE_SHOW_LOG = "cache.show.log";
    public static final String CACHE_SHOW_STAT_LOG = "cache.show.stat.log";
    public static final String CACHE_ALLOW_NULL_VALUE = "cache.allow.null.value";

    public static final String CACHE_MAXIMUM_SIZE = "cache.maximum.size";
    public static final String CACHE_EXPIRE_SECOND_AFTER_WRITE = "cache.expire.second.after.write";
    public static final String CACHE_EXPIRE_SECOND_AFTER_ACCESS = "cache.expire.second.after.access";

    private String cacheType = ConfigTools3.getString(CACHE_TYPE, CacheType.CAFFEINE.getCacheType());

    private boolean cacheShowLog = ConfigTools3.getBoolean(CACHE_SHOW_LOG, false);
    private boolean cacheShowStatLog = ConfigTools3.getBoolean(CACHE_SHOW_STAT_LOG, false);
    private boolean cacheAllowNullValue = ConfigTools3.getBoolean(CACHE_ALLOW_NULL_VALUE, true);

    private long cacheMaximumSize = ConfigTools3.getLong(CACHE_MAXIMUM_SIZE, 50000L);
    private long cacheExpireSecondAfterWrite = ConfigTools3.getLong(CACHE_EXPIRE_SECOND_AFTER_WRITE, 600L);
    private long cacheExpireSecondAfterAccess = ConfigTools3.getLong(CACHE_EXPIRE_SECOND_AFTER_ACCESS, 120L);

    private String buildPropertyKey(String basicKey, String extraKey) {
        return extraKey + "." + basicKey;
    }

    public CacheConfig(String cacheName) {
        this.cacheType = ConfigTools3.getString(buildPropertyKey(CACHE_TYPE, cacheName), cacheType);

        this.cacheShowLog = ConfigTools3.getBoolean(buildPropertyKey(CACHE_SHOW_LOG, cacheName), cacheShowLog);
        this.cacheShowStatLog = ConfigTools3.getBoolean(buildPropertyKey(CACHE_SHOW_STAT_LOG, cacheName), cacheShowStatLog);
        this.cacheAllowNullValue = ConfigTools3.getBoolean(buildPropertyKey(CACHE_ALLOW_NULL_VALUE, cacheName), cacheAllowNullValue);

        this.cacheMaximumSize = ConfigTools3.getLong(
                buildPropertyKey(CACHE_MAXIMUM_SIZE, cacheName), cacheMaximumSize);
        this.cacheExpireSecondAfterWrite = ConfigTools3.getLong(
                buildPropertyKey(CACHE_EXPIRE_SECOND_AFTER_WRITE, cacheName), cacheExpireSecondAfterWrite);
        this.cacheExpireSecondAfterAccess = ConfigTools3.getLong(
                buildPropertyKey(CACHE_EXPIRE_SECOND_AFTER_ACCESS, cacheName), cacheExpireSecondAfterAccess);
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public boolean isCacheAllowNullValue() {
        return cacheAllowNullValue;
    }

    public void setCacheAllowNullValue(boolean cacheAllowNullValue) {
        this.cacheAllowNullValue = cacheAllowNullValue;
    }

    public long getCacheMaximumSize() {
        return cacheMaximumSize;
    }

    public void setCacheMaximumSize(long cacheMaximumSize) {
        this.cacheMaximumSize = cacheMaximumSize;
    }

    public long getCacheExpireSecondAfterWrite() {
        return cacheExpireSecondAfterWrite;
    }

    public void setCacheExpireSecondAfterWrite(long cacheExpireSecondAfterWrite) {
        this.cacheExpireSecondAfterWrite = cacheExpireSecondAfterWrite;
    }

    public long getCacheExpireSecondAfterAccess() {
        return cacheExpireSecondAfterAccess;
    }

    public void setCacheExpireSecondAfterAccess(long cacheExpireSecondAfterAccess) {
        this.cacheExpireSecondAfterAccess = cacheExpireSecondAfterAccess;
    }


    public boolean isCacheShowLog() {
        return cacheShowLog;
    }

    public void setCacheShowLog(boolean cacheShowLog) {
        this.cacheShowLog = cacheShowLog;
    }

    public boolean isCacheShowStatLog() {
        return cacheShowStatLog;
    }

    public void setCacheShowStatLog(boolean cacheShowStatLog) {
        this.cacheShowStatLog = cacheShowStatLog;
    }

    @Override
    public String toString() {
        return "CacheConfig{" +
                "cacheType=" + cacheType +
                ", cacheShowLog=" + cacheShowLog +
                ", cacheShowStatLog=" + cacheShowStatLog +
                ", cacheAllowNullValue=" + cacheAllowNullValue +
                ", cacheMaximumSize=" + cacheMaximumSize +
                ", cacheExpireSecondAfterWrite=" + cacheExpireSecondAfterWrite +
                ", cacheExpireSecondAfterAccess=" + cacheExpireSecondAfterAccess +
                '}';
    }
}
