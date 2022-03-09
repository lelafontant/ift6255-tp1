import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            String dataPath = "./data";
            String indexPath = "./index";
            TaskController controller = new TaskController(dataPath, indexPath);
            // controller.createIndex();

            // TrecTest test = new TrecTest();
            // test.run();
            String file = Files.readString(Path.of("./topics/topics.1-50.txt"));
            List<String> topics = new ArrayList<>(Arrays.asList(file.split("<top>")));
            topics.remove(0);

            ArrayList<String> topicList = new ArrayList<String>();
            
            File resultFile = new File(Config.RESULT_FILE);
            boolean result = Files.deleteIfExists(resultFile.toPath());

            for (String topic : topics) {
                String part = topic.substring(topic.indexOf("<title>"), topic.indexOf("<desc>")).trim();
                String value = normalize(part.substring("<title> Topic: ".length()));
                topicList.add(value);
            }

            // controller.search(topicList.get(0));

            for (int i = 0; i < topicList.size(); i++) {
                controller.search(QueryParser.escape(topicList.get(i)), i + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }
    }

    private static String normalize(String text) {
        return text.replace("\n", " ").replace("\r", " ").replaceAll("\s+", " ").trim();
    }
}
