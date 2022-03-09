import java.io.File;
import java.io.FileFilter;

public class CsvFileFilter implements FileFilter {
    private MixFileFilter filter;

    public CsvFileFilter() {
        super();
        filter = new MixFileFilter("csv");
    }

    @Override
    public boolean accept(File pathname) {
        return filter.accept(pathname);
    }
}