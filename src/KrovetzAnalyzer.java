import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class KrovetzAnalyzer extends StopwordAnalyzerBase {
    public KrovetzAnalyzer() {
        this(CharArraySet.EMPTY_SET);
    }

    public KrovetzAnalyzer(CharArraySet stopWords) {
        super(stopWords);
    }

    public KrovetzAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(source);
        result = new StopFilter(result, stopwords);

        return new TokenStreamComponents(source, new KStemFilter(result));
    }

}
