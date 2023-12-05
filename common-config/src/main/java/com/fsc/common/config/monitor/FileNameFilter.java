package com.fsc.common.config.monitor;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.File;
import java.util.regex.Pattern;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class FileNameFilter extends AbstractFileFilter implements IFileChangedFilter {
    protected String fnp;

    public FileNameFilter(String fn) {
        this.fnp = fn;
    }

    public boolean accept(File file) {
        return !file.isFile() ? false : this.accept(this.fnp, file.getName());
    }

    public boolean accept(File dir, String name) {
        return this.accept(this.fnp, name);
    }

    public boolean accept(String fnp, String fn) {
        fnp = fnp.replace('.', '#');
        fnp = fnp.replaceAll("#", "\\\\.");
        fnp = fnp.replace('*', '#');
        fnp = fnp.replaceAll("#", ".*");
        fnp = fnp.replace('?', '#');
        fnp = fnp.replaceAll("#", ".?");
        fnp = "^" + fnp + "$";
        Pattern p = Pattern.compile(fnp);
        return p.matcher(fn).matches();
    }
}
