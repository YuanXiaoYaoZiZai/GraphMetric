package com.fsc.common.config.monitor;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleFileMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SingleFileMonitor.class);
    private FileAlterationMonitor monitor;
    private long interval = 0L;
    private Map<String, FileAlterationObserver> directoryObserverMap = Maps.newLinkedHashMap();

    public SingleFileMonitor(long interval) {
        this.interval = interval;
        if (this.interval > 0L) {
            if (this.interval < 1000L) {
                this.interval = 1000L;
            }

            this.monitor = new FileAlterationMonitor(interval);
        } else {
            this.monitor = new FileAlterationMonitor(600000L);
        }

    }

    private void load(File fn, IFileChangeListener listener) {
        listener.onFileCreate(fn);
    }

    public SingleFileMonitor monitorFile(String fn, IFileChangeListener listener) {
        if (Strings.isNullOrEmpty(fn)) {
            return this;
        } else {
            String path = FilenameUtils.getFullPath(fn);
            String name = FilenameUtils.getName(fn);
            return this.monitorFile(path, name, listener);
        }
    }

    public SingleFileMonitor monitorFile(String path, String fn, IFileChangeListener listener) {
        if (!Strings.isNullOrEmpty(path) && !Strings.isNullOrEmpty(fn)) {
            this.load(FileUtils.getFile(new String[]{path, fn}), listener);
            if (this.interval > 0L) {
                String ext = FilenameUtils.getExtension(fn);
                String baseName = FilenameUtils.getName(fn);
                IOFileFilter fileFilter = FileFilterUtils.and(new IOFileFilter[]{FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(ext), new FileNameFilter(baseName)});
                FileAlterationObserver observer = new FileAlterationObserver(FileUtils.getFile(new String[]{path}), fileFilter);
                observer.addListener(listener);
                this.monitor.addObserver(observer);
            }

            return this;
        } else {
            return this;
        }
    }

    public void start() {
        try {
            this.monitor.start();
        } catch (Exception var2) {
            logger.info("Start exception:{}", var2);
        }

    }

    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception var2) {
            logger.info("Stop exception:{}", var2);
        }

    }
}
