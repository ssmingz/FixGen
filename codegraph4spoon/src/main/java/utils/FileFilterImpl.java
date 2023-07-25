package utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FileFilterImpl implements FileFilter {
    public List<String> target = new ArrayList<>();
    @Override
    public boolean accept(File pathname) {
        if(pathname.isDirectory()) {
            pathname.listFiles(this);
        }
        if (pathname.getAbsolutePath().endsWith(".java"))
            target.add(pathname.getAbsolutePath());
        return pathname.getAbsolutePath().endsWith(".java");
    }
}
