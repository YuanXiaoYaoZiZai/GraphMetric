package com.fsc.common.config.monitor;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fsc.common.config.loader.JsonSerialize;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonChangeListener extends FileChangeListenerAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(JsonChangeListener.class);
    private Map<String, TypeReference> fileType = Maps.newHashMap();
    private Map<String, Object> fileValue = Maps.newHashMap();

    public JsonChangeListener(boolean backupAfterProcess, Map<String, TypeReference> fileType, Map<String, Object> fileValue) {
        super(backupAfterProcess);
        this.fileType = fileType;
        this.fileValue = fileValue;
    }

    public void onFileCreate(File file) {
        this.loadJson(file);
    }

    public void onFileChange(File file) {
        this.loadJson(file);
    }

    private void loadJson(File file) {
        String fn = FilenameUtils.getName(file.getName());
        TypeReference typeReference = (TypeReference)this.fileType.get(fn);
        Object v = JsonSerialize.loadJson(file, typeReference);
        if (v != null) {
            this.fileValue.put(fn, v);
        }

    }
}
