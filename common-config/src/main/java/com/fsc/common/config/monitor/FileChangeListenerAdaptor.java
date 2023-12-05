package com.fsc.common.config.monitor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileChangeListenerAdaptor extends FileAlterationListenerAdaptor implements IFileChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(FileChangeListenerAdaptor.class);
    protected boolean backupAfterProcess = false;

    public FileChangeListenerAdaptor(boolean backupAfterProcess) {
        this.backupAfterProcess = backupAfterProcess;
    }

    public void onFileCreate(File file) {
        try {
            super.onFileCreate(file);
            this.backupFile(file);
        } catch (Throwable var3) {
            logger.error("onFileCreate exception.", var3);
        }

    }

    public void onFileChange(File file) {
        try {
            super.onFileChange(file);
            this.backupFile(file);
        } catch (Throwable var3) {
            logger.error("onFileCreate exception.", var3);
        }

    }

    public void setBackupAfterProcess(boolean isBackupAfterProcess) {
        this.backupAfterProcess = isBackupAfterProcess;
    }

    public void backupFile(File file) {
        if (file.isFile()) {
            if (this.backupAfterProcess) {
                String ts = (new SimpleDateFormat("yyyyMMdd-HHmmssE")).format(new Date());
                String backupPath = FilenameUtils.getFullPath(file.getName()) + File.pathSeparator + "used";
                String fn = FilenameUtils.getName(file.getName()) + '.' + ts;
                String backupFn = backupPath + File.pathSeparator + fn;
                logger.info("Backup File src[{}] dst[{}]", file.getName(), backupFn);

                try {
                    FileUtils.forceMkdir(FileUtils.getFile(new String[]{backupPath}));
                    FileUtils.moveFile(file, FileUtils.getFile(new String[]{backupFn}));
                } catch (IOException var7) {
                    logger.info("Backup file exception:{}", var7);
                }

            }
        }
    }
}
