package com.fsc.common.config.monitor;


import org.apache.commons.io.monitor.FileAlterationListener;

public interface IFileChangeListener extends FileAlterationListener {
    void setBackupAfterProcess(boolean var1);
}
