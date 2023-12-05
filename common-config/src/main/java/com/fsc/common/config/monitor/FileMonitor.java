package com.fsc.common.config.monitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMonitor {
    private static final Logger logger = LoggerFactory.getLogger(FileMonitor.class);
    private FileAlterationMonitor monitor;
    private long interval = 0L;
    private Map<String, FileAlterationObserver> directoryObserverMap = Maps.newLinkedHashMap();

    public FileMonitor(long interval) {
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

    public FileMonitor addDirectoryListener(String filePath, Set<String> fileExt, IFileChangeListener listener) {
        return this.addDirectoryListener(filePath, fileExt, listener, false);
    }

    public FileMonitor addDirectoryListener(String filePath, Set<String> fileExt, IFileChangeListener listener, boolean recursive) {
        return this.addDirectoryListener(filePath, (IOFileFilter)(new SuffixFileFilter(Lists.newArrayList(fileExt), IOCase.INSENSITIVE)), listener, recursive);
    }

    public FileMonitor addDirectoryListener(String filePath, IOFileFilter filter, IFileChangeListener listener, boolean recursive) {
        File rootDir = FileUtils.getFile(new String[]{filePath});
        if (rootDir.isDirectory() && rootDir.exists()) {
            IOFileFilter fileFilter = null;
            if (filter == null) {
                fileFilter = FileFilterUtils.fileFileFilter();
            } else {
                fileFilter = filter;
            }

            this.loadOnStartup(listener, rootDir, fileFilter, recursive);
            if (this.interval > 0L) {
                FileAlterationObserver observer = this.getFileAlterationObserver(rootDir, fileFilter, listener);
                this.monitor.addObserver(observer);
                this.directoryObserverMap.put(rootDir.getAbsolutePath(), observer);
                logger.info("Add directory[{}] to monitor.", rootDir.getAbsolutePath());
                if (recursive) {
                    this.addSubDirectoryObserver(rootDir, fileFilter, listener);
                }
            }

            return this;
        } else {
            logger.warn("File [{}] is not Directory or not Exist.", rootDir);
            return this;
        }
    }

    private void addSubDirectoryObserver(File rootPath, IOFileFilter fileFilter, IFileChangeListener listener) {
        Collection<File> files = FileUtils.listFilesAndDirs(rootPath, FileFilterUtils.directoryFileFilter(), FileFilterUtils.directoryFileFilter());
        if (files != null && files.size() > 0) {
            Iterator var5 = files.iterator();

            while(var5.hasNext()) {
                File f = (File)var5.next();
                if (f.isDirectory() && !FilenameUtils.equals(rootPath.getAbsolutePath(), f.getAbsolutePath()) && !this.directoryObserverMap.containsKey(f.getAbsolutePath())) {
                    FileAlterationObserver observer = this.getFileAlterationObserver(f, fileFilter, listener);
                    this.monitor.addObserver(observer);
                    this.directoryObserverMap.put(f.getAbsolutePath(), observer);
                    logger.info("Add dir[{}] to monitor.", f.getAbsolutePath());
                }
            }
        }

    }

    private FileAlterationObserver getFileAlterationObserverDirAdd(File rootPath, final IOFileFilter fileFilter, final IFileChangeListener listener) {
        FileAlterationObserver dirChanged = new FileAlterationObserver(rootPath, FileFilterUtils.directoryFileFilter());
        dirChanged.addListener(new FileAlterationListenerAdaptor() {
            public void onDirectoryChange(File directory) {
                FileMonitor.this.addSubDirectoryObserver(directory, fileFilter, listener);
            }

            public void onDirectoryDelete(File directory) {
                FileAlterationObserver observer = (FileAlterationObserver)FileMonitor.this.directoryObserverMap.get(directory.getAbsolutePath());
                if (observer != null) {
                    FileMonitor.this.monitor.removeObserver(observer);
                }

            }
        });
        return dirChanged;
    }

    private void loadOnStartup(IFileChangeListener listener, File rootPath, IOFileFilter fileFilter, boolean recursive) {
        Collection<File> files;
        if (recursive) {
            files = FileUtils.listFiles(rootPath, fileFilter, FileFilterUtils.directoryFileFilter());
        } else {
            files = FileUtils.listFiles(rootPath, fileFilter, (IOFileFilter)null);
        }

        if (files != null) {
            files.forEach((e) -> {
                listener.onFileChange(e);
            });
        }

    }

    private FileAlterationObserver getFileAlterationObserver(File rootPath, IOFileFilter fileFilter, IFileChangeListener listener) {
        FileAlterationObserver observer = new FileAlterationObserver(rootPath, fileFilter);
        observer.addListener(listener);
        return observer;
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
