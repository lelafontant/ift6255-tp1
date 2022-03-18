import java.io.File;
import java.io.FileFilter;

public class TextFileFilter implements FileFilter {
    private MixFileFilter filter;

    public TextFileFilter() {
        super();
        filter = new MixFileFilter("txt");
    }

    @Override
    public boolean accept(File pathname) {
        return filter.accept(pathname);
    }
}