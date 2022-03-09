import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class MixFileFilter implements FileFilter {
    private List<String> extensions;

    public MixFileFilter() {
        super();
        extensions = new ArrayList<String>();
    }

    public MixFileFilter(String ext) {
        this();
        this.extensions.add(ext.toLowerCase());
    }

    public MixFileFilter(String[] ext) {
        this();
        for (int i = 0; i < ext.length; i++) {
            this.extensions.add(ext[i].toLowerCase());
        }
    }

    @Override
    public boolean accept(File pathname) {
        String fileName = pathname.getName().toLowerCase();
        
        int i = fileName.lastIndexOf('.');
        if (i == -1) {
            return false;
        }

        return extensions.contains(fileName.substring(i + 1));
    }
}
