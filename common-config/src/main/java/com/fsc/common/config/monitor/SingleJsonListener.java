package com.fsc.common.config.monitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fsc.common.config.loader.JsonSerialize;

import java.io.File;

public class SingleJsonListener extends FileChangeListenerAdaptor implements IFileChangeListener {
    protected TypeReference typeReference;
    protected Object value;

    public SingleJsonListener(boolean backupAfterProcess, TypeReference typeReference, Object value) {
        super(backupAfterProcess);
        this.typeReference = typeReference;
        if (value == null) {
            throw new IllegalArgumentException("Value Object Can't be NULL");
        } else {
            this.value = value;
        }
    }

    public void onFileCreate(File file) {
        this.value = this.loadJson(file);
        super.onFileCreate(file);
    }

    public void onFileChange(File file) {
        this.value = this.loadJson(file);
        super.onFileChange(file);
    }

    public Object loadJson(File file) {
        return JsonSerialize.loadJson(file, this.typeReference);
    }
}
