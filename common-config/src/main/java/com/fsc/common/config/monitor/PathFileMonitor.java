package com.fsc.common.config.monitor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathFileMonitor {
    private static final Logger logger = LoggerFactory.getLogger(PathFileMonitor.class);
    private FileAlterationMonitor monitor;
    private long interval = 0L;

    public PathFileMonitor(long interval) {
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

    public PathFileMonitor monitorPath(String filePath, String fileNamePattern, Set<String> extSet, IFileChangeListener listener, boolean recursive) {
        File root = FileUtils.getFile(new String[]{filePath});
        if (root.isDirectory() && root.exists()) {
            IOFileFilter fileFilter = FileFilterUtils.fileFileFilter();
            if (extSet != null && extSet.size() > 0) {
                fileFilter = FileFilterUtils.and(new IOFileFilter[]{fileFilter, new SuffixFileFilter(Lists.newArrayList(extSet))});
            }

            if (!Strings.isNullOrEmpty(fileNamePattern)) {
                fileFilter = FileFilterUtils.and(new IOFileFilter[]{fileFilter, new FileNameFilter(fileNamePattern)});
            }

            this.load(listener, root, fileFilter, recursive);
            if (this.interval > 0L) {
                List<File> paths = Lists.newArrayList();
                if (recursive) {
                    Collection<File> subDirs = FileUtils.listFilesAndDirs(root, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
                    paths.addAll(subDirs);
                } else {
                    paths.add(root);
                }

                Iterator var12 = paths.iterator();

                while(var12.hasNext()) {
                    File dir = (File)var12.next();
                    FileAlterationObserver observer = this.getFileAlterationObserver(dir, fileFilter, listener);
                    this.monitor.addObserver(observer);
                    logger.info("Monitor Directory[{}] Filter[Name:{} Extension:{}].", new Object[]{dir.getAbsolutePath(), fileNamePattern, extSet});
                }
            }

            return this;
        } else {
            logger.warn("File [{}] is not Directory or not Exist.", root);
            return this;
        }
    }

    private void load(IFileChangeListener listener, File rootPath, IOFileFilter fileFilter, boolean recursive) {
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
