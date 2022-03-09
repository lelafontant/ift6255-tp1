import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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

    public Indexer(String indexDirectoryPath) throws IOException {
        // this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());
        Analyzer standard = new StandardAnalyzer(new FileReader("./test/stoplist.txt"));
        // Analyzer krovetz = new KrovetzAnalyzer();
        // Analyzer porter = new PorterAnalyzer();
        // Analyzer bm25 =new Anal
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(OpenMode.CREATE);

        // create the indexer
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();

        // index file contents
        Field contentField = new TextField(LuceneConstants.CONTENTS, new FileReader(file));
        // index file name
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

        // ArrayList<String> docnos = new ArrayList<String>();
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

    
    private String normalize(String text) {
        return text.replace("\n", " ").replace("\r", " ").replaceAll("\s+", " ").trim();
    }

    private boolean isValid(File file) {
        return !file.isDirectory() && !file.isHidden() && file.exists() && file.canRead();
    }
}