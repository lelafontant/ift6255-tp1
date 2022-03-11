import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            TaskController controller = new TaskController();

            // Create index
            // controller.createIndex(Config.NIL_INDEX_DIR);
            // controller.createIndex(Config.STD_INDEX_DIR, Stemming.STANDARD);
            // controller.createIndex(Config.KTZ_INDEX_DIR, Stemming.KROVETZ);
            // controller.createIndex(Config.PTR_INDEX_DIR, Stemming.PORTER);
            // controller.createIndex(Config.STD_STOP_INDEX_DIR, Stemming.STANDARD, Config.STOPLIST_FILE);
            // controller.createIndex(Config.KTZ_STOP_INDEX_DIR, Stemming.KROVETZ, Config.STOPLIST_FILE);
            // controller.createIndex(Config.PTR_STOP_INDEX_DIR, Stemming.PORTER, Config.STOPLIST_FILE);

            // Search
            search(controller, Config.NIL_INDEX_DIR, Config.NIL_OUT_DIR);
            // search(controller, Config.STD_INDEX_DIR, Config.STD_OUT_DIR);
            // search(controller, Config.STD_STOP_INDEX_DIR, Config.STD_STOP_OUT_DIR);
            search(controller, Config.KTZ_INDEX_DIR, Config.KTZ_OUT_DIR);
            search(controller, Config.KTZ_STOP_INDEX_DIR, Config.KTZ_STOP_OUT_DIR);
            search(controller, Config.PTR_INDEX_DIR, Config.PTR_OUT_DIR);
            search(controller, Config.PTR_STOP_INDEX_DIR, Config.PTR_STOP_OUT_DIR);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }
    }

    private static void search(TaskController controller, String indexPath, String outRoot) {
        controller.search(indexPath, Path.of(outRoot, "bm25", "results1.txt"), Config.TOPIC1_FILE, RetrievalModel.BM25, 1);
        controller.search(indexPath, Path.of(outRoot, "bm25", "results2.txt"), Config.TOPIC2_FILE, RetrievalModel.BM25, 51);
        controller.search(indexPath, Path.of(outRoot, "bm25", "results3.txt"), Config.TOPIC3_FILE, RetrievalModel.BM25, 101);
        controller.search(indexPath, Path.of(outRoot, "lm", "results1.txt"), Config.TOPIC1_FILE, RetrievalModel.LM, 1);
        controller.search(indexPath, Path.of(outRoot, "lm", "results2.txt"), Config.TOPIC2_FILE, RetrievalModel.LM, 51);
        controller.search(indexPath, Path.of(outRoot, "lm", "results3.txt"), Config.TOPIC3_FILE, RetrievalModel.LM, 101);
        controller.search(indexPath, Path.of(outRoot, "tfidf", "results1.txt"), Config.TOPIC1_FILE, RetrievalModel.TFIDF, 1);
        controller.search(indexPath, Path.of(outRoot, "tfidf", "results2.txt"), Config.TOPIC2_FILE, RetrievalModel.TFIDF, 51);
        controller.search(indexPath, Path.of(outRoot, "tfidf", "results3.txt"), Config.TOPIC3_FILE, RetrievalModel.TFIDF, 101);
    }
}
