import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class TaskController {
    private String dataDir;
    private String indexDir;
    private Indexer indexer;
    private Searcher searcher;

    public TaskController() {

    }

    public TaskController(String dataDir, String indexDir) {
        this.dataDir = dataDir;
        this.indexDir = indexDir;
    }

    public void createIndex() throws IOException {
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(dataDir);
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
    }

    public void search(String searchQuery,int id) throws IOException, ParseException {
        searcher = new Searcher(indexDir);
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery);

        long endTime = System.currentTimeMillis();

        // System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
        // PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
   
        try(FileWriter fw = new FileWriter(Config.RESULT_FILE, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
    
                out.println(id + " " + 0 + " " + doc.get(LuceneConstants.DOC_NO));
            }
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

        searcher.close();
    }

}
