package com.fei.mcresweb.restservice.search;

import lombok.NonNull;
import lombok.val;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 搜索结果
 *
 * @param hits    命中数量 / 异常结果 (正常命中为数字, 异常结果以<code>error</code>开头)
 * @param hasMore 是否还有更多结果
 * @param docs    搜索内容
 */
public record SearchResult(@NonNull String hits, boolean hasMore, Collection<DocResult> docs) {
    /**
     * 返回结果最大长度, 超过此长度将强制截断并在尾部添加{@link #ellipsis 省略号}
     */
    public static final int maxLength = 200;
    public static final String ellipsis = "...";

    /**
     * 通过异常构建搜索结果
     *
     * @param err 错误
     * @return 异常搜索结果
     */
    @Contract("_ -> new")
    public static @NotNull SearchResult byError(@Nullable String err) {
        return new SearchResult(err == null ? "error" : ("error: " + err), false, null);
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull SearchResult fromLucene(@NonNull TopDocs topDocs, @NonNull IndexReader reader,
        @NonNull Highlighter highlighter, @NonNull Analyzer analyzer, @NonNull String idField, String... fields)
        throws IOException, InvalidTokenOffsetsException {
        val hits = topDocs.totalHits;
        val scoreDocs = topDocs.scoreDocs;
        val docResults = new ArrayList<DocResult>(scoreDocs.length);

        for (val scoreDoc : scoreDocs)
            docResults.add(DocResult.fromLucene(scoreDoc, reader, highlighter, analyzer, idField, fields));

        return new SearchResult(Long.toString(hits.value), hits.relation == TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO,
            docResults);
    }

    /**
     * @param doc   内容
     * @param score 得分
     */
    public record DocResult(Map<String, Object> doc, String id, float score) {

        @Contract("_, _, _, _, _, _ -> new")
        public static @NotNull DocResult fromLucene(@NotNull ScoreDoc scoreDoc, @NotNull IndexReader reader,
            @NonNull Highlighter highlighter, @NonNull Analyzer analyzer, @NonNull String idField,
            @NotNull String @NotNull ... fields) throws IOException, InvalidTokenOffsetsException {
            val fieldsMap = new LinkedHashMap<String, Object>();
            val doc = reader.document(scoreDoc.doc);
            for (val field : fields) {
                val value = doc.getValues(field);
                if (value != null && value.length > 0) {
                    for (int i = 0; i < value.length; i++) {
                        val v = highlighter.getBestFragment(analyzer, field, value[i]);
                        if (v != null)
                            value[i] = v;
                        if (value[i].length() > maxLength)
                            value[i] = value[i].substring(0, maxLength) + ellipsis;
                    }
                    if (value.length > 1)
                        fieldsMap.put(field, value);
                    else
                        fieldsMap.put(field, value[0]);
                }
            }
            return new DocResult(fieldsMap, String.join("", doc.getValues(idField)), scoreDoc.score);
        }
    }
}
