import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    private IndexWriter writer;
    private String dirPath;

    public Indexer(String indexDirectoryPath) throws IOException {
        this.setDirPath(indexDirectoryPath);
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());

        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(OpenMode.CREATE);

        // create the indexer
        writer = new IndexWriter(indexDirectory, config);
    }  

    public Indexer(String indexDirectoryPath, Stemming stemming) throws IOException {
        this.setDirPath(indexDirectoryPath);
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());

        Analyzer analyzer = createAnalyzer(stemming);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE);

        // create the indexer
        writer = new IndexWriter(indexDirectory, config);
    }

    public Indexer(String indexDirectoryPath, Stemming stemming, String stopListPath) throws IOException {
        this.setDirPath(indexDirectoryPath);
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());
        
        Analyzer analyzer = createAnalyzer(stemming, stopListPath);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE);

        // create the indexer
        writer = new IndexWriter(indexDirectory, config);
    }


    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();

        Field contentField = new TextField(LuceneConstants.CONTENTS, new FileReader(file));
        Field fileNameField = new TextField(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES);
        Field filePathField = new TextField(LuceneConstants.FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private Document getDocument(File file, String content) throws IOException {
        Document document = new Document();

        String docno = normalize(content.split("<DOCNO>")[1].split("</DOCNO>")[0]);
        String fileid = normalize(content.split("<FILEID>")[1].split("</FILEID>")[0]);

        Field contentField = new TextField(LuceneConstants.CONTENTS, content, Field.Store.NO);
        Field docNoField = new TextField(LuceneConstants.DOC_NO, docno, Field.Store.YES);
        Field fileIdField = new TextField(LuceneConstants.FILE_ID, fileid, Field.Store.YES);
        Field fileNameField = new TextField(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES);
        Field filePathField = new TextField(LuceneConstants.FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

        document.add(contentField);
        document.add(docNoField);
        document.add(fileIdField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private void indexFile(File file) throws IOException {
        // System.out.println("Indexing " + file.getName() + "...");
        String collection = Files.readString(Path.of(file.getPath()), StandardCharsets.ISO_8859_1);
        List<String> docs = new ArrayList<>(Arrays.asList(collection.split("<DOC>")));
        docs.remove(0);

        for (String doc : docs) {
            String content = doc.split("</DOC>")[0];

            Document document = getDocument(file, content);
            writer.addDocument(document);
        }
    }

    public int createIndex(String dataDirPath) throws IOException {
        // get all files in the data directory

        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if (isValid(file)) {
                indexFile(file);
            }
        }
        return writer.numRamDocs();
    }

    public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
        // get all files in the data directory

        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if (isValid(file) && filter.accept(file)) {
                indexFile(file);
            }
        }
        return writer.numRamDocs();
    }

    private Analyzer createAnalyzer(Stemming stemming) {
        switch (stemming) {
            case STANDARD:
                return new StandardAnalyzer();
            case KROVETZ:
                return new KrovetzAnalyzer();
            case PORTER:
                return new PorterAnalyzer();
            default:
                return new StandardAnalyzer();
        }
    }

    private Analyzer createAnalyzer(Stemming stemming, String stopListPath) throws IOException {
        Reader stopListReader = new FileReader(stopListPath);

        switch (stemming) {
            case STANDARD:
                return new StandardAnalyzer(stopListReader);
            case KROVETZ:
                return new KrovetzAnalyzer(stopListReader);
            case PORTER:
                return new PorterAnalyzer(stopListReader);
            default:
                return new StandardAnalyzer(stopListReader);
        }
    }

    private String normalize(String text) {
        return text.replace("\n", " ").replace("\r", " ").replaceAll("\s+", " ").trim();
    }

    private boolean isValid(File file) {
        return !file.isDirectory() && !file.isHidden() && file.exists() && file.canRead();
    }
}