package com.fsc.common.config.monitor;

import com.fsc.common.config.loader.PropertiesLoader2;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyFileListener extends FileChangeListenerAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFileListener.class);
    private Map<String, String> properties = Maps.newLinkedHashMap();

    public PropertyFileListener(boolean backupAfterProcess, Map<String, String> properties) {
        super(backupAfterProcess);
        this.properties = properties;
    }

    public void onFileCreate(File file) {
        logger.info("Property Config File Create:[{}]", file.getAbsolutePath());
        this.loadProperty(file);
    }

    public void onFileChange(File file) {
        logger.info("Property Config File Changed:[{}]", file.getAbsolutePath());
        this.loadProperty(file);
    }

    public void onFileDelete(File file) {
        logger.info("Property Config File Delete:[{}]", file.getAbsolutePath());
    }

    private void loadProperty(File file) {
        try {
            Map<String, String> propertyMap = PropertiesLoader2.loader(file);
            propertyMap.forEach((k, v) -> {
                if (this.properties.containsKey(k)) {
                    logger.info("Use new properties :[{}]=[{}]", k, v);
                }

                this.properties.put(k, v);
            });
        } catch (IOException var3) {
            logger.info("Load properties[{}] exception:{}", file.getAbsolutePath(), var3);
        }

    }
}

