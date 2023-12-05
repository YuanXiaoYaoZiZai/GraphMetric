package com.fsc.common.config;

import com.fsc.common.config.aes.AESTools;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;

public class ConfigAESTools extends ConfigTools3 {
    public ConfigAESTools() {
    }

    public static String getConfigAsString(String key, String aeskey, String defaultValue) {
        String v = getConfigAsString(key, aeskey);
        return Strings.isNullOrEmpty(v) ? defaultValue : v;
    }

    public static String getConfigAsString(String key, String aeskey) {
        String original = getConfigAsString(key);
        if (Strings.isNullOrEmpty(original)) {
            return "";
        } else {
            try {
                return AESTools.decrypt(original, aeskey);
            } catch (Exception var4) {
                return "";
            }
        }
    }

    public static List<String> getConfigAsList(String key, String aeskey, String split) {
        String value = getConfigAsString(key, aeskey);
        return (List)(Strings.isNullOrEmpty(value) ? Lists.newArrayList() : Splitter.on(split).omitEmptyStrings().splitToList(value));
    }

    public static List<String> getConfigAsList(String key, String aeskey) {
        return getConfigAsList(key, ",");
    }
}
