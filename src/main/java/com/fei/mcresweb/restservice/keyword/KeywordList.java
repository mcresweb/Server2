package com.fei.mcresweb.restservice.keyword;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

/**
 * 会员码列表
 *
 * @param page   总页码
 * @param amount 总数量
 * @param list   会员码列表
 */
public record KeywordList(int page, long amount, List<Keyword> list) {
    public static final KeywordList EMPTY = new KeywordList(0, 0, Collections.emptyList());

    @Contract("_ -> new")
    public static @NotNull KeywordList valueOf(@NotNull Page<com.fei.mcresweb.dao.Keyword> keywords) {
        return new KeywordList(keywords.getTotalPages(), keywords.getTotalElements(),
            keywords.stream().map(Keyword::new).toList());
    }

    /**
     * @param token      令牌
     * @param summonTime 生成时间
     * @param summoner   生成者
     * @param used       是否已经
     * @param user       使用者
     * @param usedTime   使用时间
     * @param value      价值
     * @param expire     过期时间
     */
    public record Keyword(@NonNull String token, long summonTime, int summoner, boolean used, Integer user,
                          Long usedTime, int value, Long expire) {
        public Keyword(@NotNull com.fei.mcresweb.dao.Keyword kw) {
            this(kw.getId(), kw.getGenerateTime().getTime(), kw.getGenerateUserID(), kw.isUsed(), kw.getUserID(),
                kw.getUseTime() == null ? null : kw.getUseTime().getTime(), kw.getValue(),
                kw.getExpire() == null ? null : kw.getExpire().getTime());
        }
    }
}
