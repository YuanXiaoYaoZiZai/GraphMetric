package com.fsc.common.config;

import com.fsc.common.config.monitor.FileMonitor;
import com.fsc.common.config.monitor.IFileChangeListener;
import com.fsc.common.config.monitor.PropertyFileListener;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.Set;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigTools2 extends AbstractConfigTools {
    private static final Logger logger = LoggerFactory.getLogger(ConfigTools2.class);

    public ConfigTools2() {
    }

    public static void load(Set<String> paths, Set<String> extensionSet) {
        load(paths, extensionSet, interval, true, new PropertyFileListener(false, configMap));
    }

    public static void load(String path, Set<String> extensionSet) {
        if (extensionSet == null || ((Set)extensionSet).size() == 0) {
            extensionSet = ImmutableSet.of("config", "properties");
        }

        Set<String> configRoot = Sets.newHashSet();
        if (Strings.isNullOrEmpty(path)) {
            configRoot = getDefaultPath();
        } else {
            configRoot = Sets.newHashSet(new String[]{path});
        }

        load((Set)configRoot, (Set)extensionSet);
    }

    public static void load(String path) {
        load((String)path, (Set)null);
    }

    public static void load() {
        load((String)null);
    }

    public static void load(Set<String> paths, Set<String> extensionSet, long interval, boolean recursive, IFileChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener is required.");
        } else {
            FileMonitor fileMonitor = new FileMonitor(interval);
            paths.forEach((path) -> {
                fileMonitor.addDirectoryListener(path, extensionSet, listener, true);
            });
            fileMonitor.start();
        }
    }

    public static void load(String path, long interval, boolean recursive, IOFileFilter filter, IFileChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener is required.");
        } else {
            FileMonitor fileMonitor = new FileMonitor(interval);
            fileMonitor.addDirectoryListener(path, filter, listener, true);
            fileMonitor.start();
        }
    }

    public static void loadJson(Set<String> paths, long interval, boolean recursive, IFileChangeListener listener) {
        load(paths, ImmutableSet.of("json"), interval, recursive, listener);
    }

    public static void loadJson(String paths, long interval, boolean recursive, IFileChangeListener listener) {
        loadJson((Set)ImmutableSet.of(paths), interval, recursive, listener);
    }

    /** @deprecated */
    @Deprecated
    public static void loadProperty(Set<String> paths, long interval, boolean recursive, IFileChangeListener listener) {
        load(paths, ImmutableSet.of("config", "properties"), interval, recursive, listener);
    }

    private static Set<String> getDefaultPath() {
        Set<String> defPathSet = Sets.newLinkedHashSet();
        defPathSet.add(getUserHomeConfigPath());
        defPathSet.add("config");
        return defPathSet;
    }

    private static String getUserHomeConfigPath() {
        String defPath = System.getProperty("user.home");
        if (Strings.isNullOrEmpty(defPath)) {
            defPath = "config";
        } else {
            if (!defPath.endsWith(File.separator)) {
                defPath = defPath + File.separator;
            }

            defPath = defPath + "config_tool" + File.separator + "config";
        }

        return defPath;
    }
}

