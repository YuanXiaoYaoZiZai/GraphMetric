package com.fsc.common.cache;

import com.fsc.common.utils.AbstractPrintable;

public class CacheStat extends AbstractPrintable {

    private long reqCount;
    private long hitCount;
    private double hitRate;
    private long missCount;
    private double missRate;
    private long loadCount;
    private long loadSuccessCount;
    private long loadFailureCount;
    private long totalLoadTime;
    private long evictionCount;
    private long evictionWeight;

    public long getReqCount() {
        return reqCount;
    }

    public void setReqCount(long reqCount) {
        this.reqCount = reqCount;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public double getHitRate() {
        return hitRate;
    }

    public void setHitRate(double hitRate) {
        this.hitRate = hitRate;
    }

    public long getMissCount() {
        return missCount;
    }

    public void setMissCount(long missCount) {
        this.missCount = missCount;
    }

    public double getMissRate() {
        return missRate;
    }

    public void setMissRate(double missRate) {
        this.missRate = missRate;
    }

    public long getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(long loadCount) {
        this.loadCount = loadCount;
    }

    public long getLoadSuccessCount() {
        return loadSuccessCount;
    }

    public void setLoadSuccessCount(long loadSuccessCount) {
        this.loadSuccessCount = loadSuccessCount;
    }

    public long getLoadFailureCount() {
        return loadFailureCount;
    }

    public void setLoadFailureCount(long loadFailureCount) {
        this.loadFailureCount = loadFailureCount;
    }

    public long getTotalLoadTime() {
        return totalLoadTime;
    }

    public void setTotalLoadTime(long totalLoadTime) {
        this.totalLoadTime = totalLoadTime;
    }

    public long getEvictionCount() {
        return evictionCount;
    }

    public void setEvictionCount(long evictionCount) {
        this.evictionCount = evictionCount;
    }

    public long getEvictionWeight() {
        return evictionWeight;
    }

    public void setEvictionWeight(long evictionWeight) {
        this.evictionWeight = evictionWeight;
    }
}
