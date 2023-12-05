package com.fsc.common.cache;

public enum CacheType {

    CAFFEINE("CAFFEINE");

    private String cacheType;

    CacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public String getCacheType() {
        return cacheType;
    }

}
