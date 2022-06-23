package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.dao.Essay;
import com.fei.mcresweb.dao.EssayDao;
import com.fei.mcresweb.restservice.search.SearchResult;
import lombok.NonNull;
import lombok.val;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * 内容搜索服务
 */
@Service
public class ContentSearchServiceImpl implements ContentSearchService {

    private static final Logger log = Logger.getLogger(ContentSearchServiceImpl.class.getName());
    /**
     * 是否需要初始化
     */
    private final boolean needInit;
    /**
     * 分词器
     */
    private final Analyzer analyzer;

    /**
     * 前端地址
     */
    private String webUrl;

    @Value("${mrw.web-url}/api/convert/")
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public static final Path dirPath = Paths.get("runtime", "index-essay");
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;
    private final EssayDao essayDao;
    private final Directory directory;
    private final IndexWriter indexWriter;

    /**
     * 内容解析器
     */
    private final MultiFieldQueryParser essayParser;
    /**
     * 用户解析器
     */
    private final MultiFieldQueryParser userParser;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @PreDestroy
    public void beforeDestroy() {
        try {
            indexWriter.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            val reader = indexReader;
            if (reader != null)
                reader.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() throws IOException {
        template.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        if (needInit)
            rebuildIndex();
        flushWrite(null);
    }

    public ContentSearchServiceImpl(EssayDao essayDao) throws IOException {
        this.essayDao = essayDao;

        needInit = !Files.isDirectory(dirPath);

        analyzer = new StandardAnalyzer();
        essayParser = new MultiFieldQueryParser(new String[] {//
            Essay.Fields.id,//
            Essay.Fields.title,//
            Essay.Fields.tags,//
            Essay.Fields.content,//
            Essay.Fields.description,//
        }, analyzer);
        userParser = new MultiFieldQueryParser(new String[] {//
            Essay.Fields.sender,//
            Essay.Fields.senderID,//
        }, analyzer);
        directory = FSDirectory.open(dirPath);
        indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));

    }

    @Override
    public void writeEssay(@NonNull Essay essay) throws IOException {
        writeEssay(essay, true);
    }

    @Override
    public int writeEssays(@NonNull Iterable<Essay> essays) throws IOException {
        int cnt = 0;
        for (val essay : essays) {
            writeEssay(essay, false);
            cnt++;
        }
        flushWrite(null);
        return cnt;
    }

    private void writeEssay(@NonNull Essay essay, boolean flush) throws IOException {
        Document document = new Document();
        document.add(new StringField(Essay.Fields.id, Integer.toString(essay.getId()), Field.Store.YES));
        document.add(new TextField(Essay.Fields.title, essay.getTitle(), Field.Store.YES));
        val tags = essay.getTagsList();
        if (tags != null)
            Arrays.stream(tags).map(tag -> new StringField(Essay.Fields.tags, tag, Field.Store.YES))
                .forEach(document::add);
        document.add(
            new TextField(Essay.Fields.content, transContent(essay.getContent(), essay.getType()), Field.Store.YES));
        document.add(new StringField(Essay.Fields.catalogueKey, essay.getCatalogueKey(), Field.Store.YES));
        document.add(new StringField(Essay.Fields.categoryKey, essay.getCategoryKey(), Field.Store.YES));
        val description = essay.getDescription();
        if (description != null)
            document.add(new TextField(Essay.Fields.description, description, Field.Store.YES));
        document.add(new TextField(Essay.Fields.senderID, Integer.toString(essay.getSenderID()), Field.Store.YES));
        document.add(new TextField(Essay.Fields.sender, essay.getSender().getUsername(), Field.Store.YES));

        indexWriter.deleteDocuments(new Term(Essay.Fields.id, Integer.toString(essay.getId())));
        if (flush)
            flushWrite(document);
        else
            indexWriter.addDocument(document);
    }

    /**
     * 内容转换响应
     *
     * @param txt  转换内容
     * @param html 内容是否是html
     */
    private record ContentConvertResp(String txt, boolean html) {
    }

    /**
     * http请求器
     */
    private final RestTemplate template = new RestTemplate();

    /**
     * 翻译内容为纯文本
     *
     * @param content 内容
     * @param type    内容类型
     * @return 过滤后的纯文本内容
     */
    private String transContent(String content, String type) {
        val resp = template.postForObject(webUrl + type, content, ContentConvertResp.class);
        if (resp == null)
            throw new DataAccessResourceFailureException("Can not convert essay content");

        return resp.html() ? Jsoup.parse(resp.txt()).text() : resp.txt();
    }

    @Override
    public int rebuildIndex() throws IOException {
        log.info("Rebuild Index!");
        val all = essayDao.findAll();
        indexWriter.deleteAll();
        return writeEssays(all);
    }

    private static final String[] essayFields =
        new String[] {Essay.Fields.id, Essay.Fields.title, Essay.Fields.tags, Essay.Fields.content,
            Essay.Fields.description, Essay.Fields.senderID};

    @Override
    public @NotNull SearchResult searchEssay(@NonNull String str, @Nullable String catalogue, @Nullable String category,
        @Nullable String sender) throws ParseException, IOException {
        final Query q;
        try {
            q = essayParser.parse(str);
        } catch (Exception e) {
            return SearchResult.byError(e.getMessage());
        }
        if (!Tool.valid(catalogue) && !Tool.valid(category) && !Tool.valid(sender))
            return search(q, 10, Essay.Fields.id, essayFields);
        val builder = new BooleanQuery.Builder();
        builder.add(q, BooleanClause.Occur.MUST);

        if (Tool.valid(sender)) {
            final Query qu;
            try {
                qu = userParser.parse(sender);
            } catch (Exception e) {
                return SearchResult.byError(e.getMessage());
            }
            builder.add(qu, BooleanClause.Occur.MUST);
        }
        if (Tool.valid(catalogue))
            builder.add(new TermQuery(new Term(Essay.Fields.catalogueKey, catalogue)), BooleanClause.Occur.MUST);
        if (Tool.valid(category))
            builder.add(new TermQuery(new Term(Essay.Fields.categoryKey, category)), BooleanClause.Occur.MUST);

        return search(builder.build(), 15, Essay.Fields.id, essayFields);
    }

    private @NotNull SearchResult search(Query query, int amount, String idField, String... fields) throws IOException {
        lock.readLock().lock();
        try {
            val topDocs = getSearcher().search(query, amount);
            Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<b>", "</b>"), new QueryScorer(query));
            return SearchResult.fromLucene(topDocs, indexReader, highlighter, analyzer, idField, fields);
        } catch (InvalidTokenOffsetsException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void flushWrite(@Nullable Document document) throws IOException {
        lock.writeLock().lock();
        try {
            if (document != null)
                indexWriter.addDocument(document);
            indexWriter.commit();
            indexSearcher = null;
            if (indexReader != null)
                try {
                    indexReader.close();
                } catch (IOException ignore) {
                }
            indexReader = null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private IndexSearcher getSearcher() throws IOException {
        lock.readLock().lock();
        try {
            if (indexSearcher != null)
                return indexSearcher;
            synchronized (lock) {
                if (indexSearcher != null)
                    return indexSearcher;
                indexReader = DirectoryReader.open(directory);
                indexSearcher = new IndexSearcher(indexReader);
            }
            return indexSearcher;
        } finally {
            lock.readLock().unlock();
        }

    }

}
