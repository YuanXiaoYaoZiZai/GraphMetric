package com.fsc.common.config;

import com.fsc.common.config.monitor.PathFileMonitor;
import com.fsc.common.config.monitor.PropertyFileListener;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ConfigTools3 extends ConfigTools2 {
    public ConfigTools3() {
    }

    public static void load(String filePath, String fileNamePattern, Set<String> extSet, boolean recursive) {
        (new PathFileMonitor(interval)).monitorPath(replaceDirectoryPath(filePath), fileNamePattern, extSet, new PropertyFileListener(false, configMap), recursive).start();
    }

    public static void load(String dir, Set<String> extSet) {
        load(dir, (String)null, extSet, true);
    }

    public static void load(String dir) {
        load(dir, (String)null, ImmutableSet.of("config", "properties"), true);
    }

    public static void load() {
        load("config");
    }
}

