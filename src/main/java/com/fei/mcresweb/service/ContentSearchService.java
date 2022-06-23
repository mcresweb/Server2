package com.fei.mcresweb.service;

import com.fei.mcresweb.dao.Essay;
import com.fei.mcresweb.restservice.search.SearchResult;
import lombok.NonNull;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 内容搜索服务
 */
public interface ContentSearchService {

    /**
     * 搜索内容<br>在全字段搜索内容
     *
     * @param str 搜索文字
     * @return 搜索结果
     * @throws ParseException 解析异常
     * @throws IOException    IO异常
     */
    @NotNull SearchResult searchEssay(@NonNull String str, @Nullable String catalogue, @Nullable String category,
        @Nullable String sender) throws ParseException, IOException;

    /**
     * 写入一个essay索引
     *
     * @param essay 内容数据
     * @throws IOException IO异常
     */
    void writeEssay(@NonNull Essay essay) throws IOException;

    /**
     * 写入多个essay索引
     *
     * @param essays 内容数据
     * @throws IOException IO异常
     */
    int writeEssays(@NonNull Iterable<Essay> essays) throws IOException;

    /**
     * 重建索引
     *
     * @throws IOException IO异常
     */
    int rebuildIndex() throws IOException;
}
