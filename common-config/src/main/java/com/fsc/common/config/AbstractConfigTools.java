package com.fsc.common.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigTools {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfigTools.class);
    private static final String SPLIT_STR = ",";
    protected static Map<String, String> configMap = Maps.newLinkedHashMap();
    protected static long interval = 60000L;
    protected static boolean enableEnv = true;

    public AbstractConfigTools() {
    }

    public static void enableEnv(boolean enableEnv) {
        AbstractConfigTools.enableEnv = enableEnv;
    }

    public static void setInterval(long interval) {
        AbstractConfigTools.interval = interval;
    }

    public static String getString(String key, String defaultValue) {
        return getConfigAsString(key, defaultValue);
    }

    public static String getString(String key) {
        return getConfigAsString(key);
    }

    public static List<String> getAsList(String key, String split) {
        return getConfigAsList(key, split);
    }

    public static List<String> getAsList(String key) {
        return getAsList(key, ",");
    }

    public static Long getLong(String key) {
        return getLong(key, (Long)null);
    }

    public static Long getLong(String key, Long defaultValue) {
        return getConfigAsLong(key, defaultValue);
    }

    public static Integer getInt(String key, Integer defaultValue) {
        return getConfigAsInt(key, defaultValue);
    }

    public static Integer getInt(String key) {
        return getInt(key, (Integer)null);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getAsBoolean(key, defaultValue);
    }

    public static String getConfigAsString(String key, String defaultValue) {
        String value = getConfigAsString(key);
        return Strings.isNullOrEmpty(value) ? defaultValue : value;
    }

    public static String getConfigAsString(String key) {
        String value = (String)configMap.get(key);
        if (value == null && enableEnv) {
            value = System.getenv(key);
        }

        return Strings.nullToEmpty(value);
    }

    public static List<String> getConfigAsList(String key, String split) {
        String value = getConfigAsString(key);
        return (List)(Strings.isNullOrEmpty(value) ? Lists.newArrayList() : Splitter.on(split).omitEmptyStrings().splitToList(value));
    }

    public static List<String> getConfigAsList(String key) {
        return getConfigAsList(key, ",");
    }

    public static Long getConfigAsLong(String key) {
        return getConfigAsLong(key, (Long)null);
    }

    public static Long getConfigAsLong(String key, Long defaultValue) {
        try {
            return Long.parseLong(getConfigAsString(key));
        } catch (NumberFormatException var3) {
            return defaultValue;
        }
    }

    public static Integer getConfigAsInt(String key, Integer defaultValue) {
        try {
            return Integer.parseInt(getConfigAsString(key));
        } catch (NumberFormatException var3) {
            return defaultValue;
        }
    }

    public static Integer getConfigAsInt(String key) {
        return getConfigAsInt(key, (Integer)null);
    }

    public static boolean getAsBoolean(String key, boolean defaultValue) {
        String value = getConfigAsString(key);
        if (Strings.isNullOrEmpty(value)) {
            return defaultValue;
        } else {
            return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "y".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value);
        }
    }

    public static Map<String, String> getAllConfig() {
        return configMap;
    }

    public static String replaceDirectoryPath(String path) {
        return Strings.isNullOrEmpty(path) ? path : path.replace("${user.home}", FileUtils.getUserDirectoryPath()).replace("${java.io.tmpdir}", FileUtils.getTempDirectoryPath());
    }
}
