import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.lucene.benchmark.quality.Judge;
import org.apache.lucene.benchmark.quality.QualityBenchmark;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.QualityStats;
import org.apache.lucene.benchmark.quality.trec.TrecJudge;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.benchmark.quality.utils.SimpleQQParser;
import org.apache.lucene.benchmark.quality.utils.SubmissionReport;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TrecTest {
    public TrecTest() {
        super();
    }

    public void run() {
        try {
            File topicsFile = new File("./topics/topics.1-50.txt");
            File qrelsFile = new File("./test/qrels.1-50.AP8890.txt");
            Directory dir = FSDirectory.open(new File("./index/").toPath());
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);

            String docNameField = "docno";

            PrintWriter logger = new PrintWriter(System.out, true);

            TrecTopicsReader qReader = new TrecTopicsReader();
            QualityQuery qqs[] = qReader.readQueries(new BufferedReader(new FileReader(topicsFile)));

            Judge judge = new TrecJudge(new BufferedReader(new FileReader(qrelsFile)));

            judge.validateData(qqs, logger);

            QualityQueryParser qqParser = new SimpleQQParser("title", "contents");

            QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
            SubmissionReport submitLog = null;
            QualityStats stats[] = qrun.execute(judge, submitLog, logger);

            QualityStats avg = QualityStats.average(stats);
            avg.log("SUMMARY", 2, logger, "  ");
            dir.close();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
