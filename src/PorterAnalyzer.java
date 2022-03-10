import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class PorterAnalyzer extends StopwordAnalyzerBase {
    public PorterAnalyzer() {
        this(CharArraySet.EMPTY_SET);
    }

    public PorterAnalyzer(CharArraySet stopWords) {
        super(stopWords);
    }

    public PorterAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer src = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(src);
        result = new StopFilter(result, stopwords);

        return new TokenStreamComponents(src, new PorterStemFilter(result));
    }

}
