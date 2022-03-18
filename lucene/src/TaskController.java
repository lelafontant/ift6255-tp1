import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class TaskController {
    public TaskController() {

    }

    public Indexer createIndex(String indexDir) throws IOException {
        Indexer indexer = new Indexer(indexDir);
        buildIndex(indexer);

        return indexer;
    }

    public Indexer createIndex(String indexDir, Stemming stemming) throws IOException {
        Indexer indexer = new Indexer(indexDir, stemming);
        buildIndex(indexer);

        return indexer;
    }

    public Indexer createIndex(String indexDir, Stemming stemming, String stopListPath) throws IOException {
        Indexer indexer = new Indexer(indexDir, stemming, stopListPath);
        buildIndex(indexer);

        return indexer;
    }

    public void search(String indexPath, String query, int id, RetrievalModel model) throws IOException, ParseException {
        Searcher searcher = new Searcher(indexPath);
        TopDocs hits = searchWith(searcher, query, model);

        int i = 1;
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);

            System.out.println(printResult(id, doc.get(LuceneConstants.DOC_NO), i, scoreDoc.score));
            
            i++;
        }
    }

    public void search(String indexPath, Path outPath, String query, int id, RetrievalModel model) throws IOException, ParseException {
        Searcher searcher = new Searcher(indexPath);
        TopDocs hits = searchWith(searcher, query, model);

        try (FileWriter fw = new FileWriter(outPath.toString(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            int i = 1;
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);

                out.println(printResult(id, doc.get(LuceneConstants.DOC_NO), i, scoreDoc.score));
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }
    }

    public void search(String indexPath, Path outPath, List<String> topics, RetrievalModel model, int start) throws IOException, ParseException {
        ArrayList<String> topicTitles = new ArrayList<String>();
        for (String topic : topics) {
            String part = topic.substring(topic.indexOf("<title>"), topic.indexOf("<desc>")).trim();
            String value = normalize(part.substring("<title> Topic: ".length()));
            topicTitles.add(value);
        }

        Files.deleteIfExists(outPath);

        for (int i = 0; i < topicTitles.size(); i++) {
            search(indexPath, outPath, QueryParser.escape(topicTitles.get(i)), start + i , model);
        }
    }

    public void search(String indexPath, Path outPath, String topicsPath, RetrievalModel model, int start) {
        try {
            search(indexPath, outPath, getTopics(topicsPath), model, start);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }       
    }

    private List<String> getTopics(String filePath) throws Exception {
        String file = Files.readString(Path.of(filePath));
        List<String> topics = new ArrayList<>(Arrays.asList(file.split("<top>")));
        topics.remove(0);

        return topics;
    }

    private String normalize(String text) {
        return text.replace("\n", " ").replace("\r", " ").replaceAll("\s+", " ").trim();
    }

    private TopDocs searchWith(Searcher searcher, String query, RetrievalModel model) throws IOException, ParseException {
        switch (model) {
            case BM25:
                return searcher.searchBM25(query);
            case TFIDF:
                return searcher.searchTFIDF(query);
            case LM:
                return searcher.searchLM(query);
            default:
                return searcher.search(query);
        }
    }

    private void buildIndex(Indexer indexer) throws IOException {
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(Config.DATA_DIR);
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
    }

    private String printResult(int id, String docNo, int index, float score) {
        return id + " " + 0 + " " + docNo + " " + index + " " + score + " " + "lucene";
    }
}
