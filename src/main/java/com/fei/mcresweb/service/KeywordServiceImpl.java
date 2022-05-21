package com.fei.mcresweb.service;

import com.fei.mcresweb.dao.Keyword;
import com.fei.mcresweb.dao.KeywordDao;
import com.fei.mcresweb.defs.TokenLen;
import com.fei.mcresweb.restservice.keyword.KeywordList;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * 会员码服务
 */
@Service
public class KeywordServiceImpl implements KeywordService {
    /**
     * TOKEN长度
     */
    public static final int TOKEN_LEN = TokenLen.TOKEN_LEN;
    private final Random random = new SecureRandom();
    @Autowired
    private KeywordDao keywordDao;

    @Override
    public String[] summon(Integer user, @NotNull SummonReq req) {
        //TODO admin check
        val expire = req.expire() == null ? null : new Date(req.expire());
        val list = IntStream//
            .range(0, req.amount())//
            .mapToObj(i -> summonToken(user, i))//
            .map(token -> new Keyword(token, user, req.value(), expire))//
            .toList();
        keywordDao.saveAll(list);
        return list.stream().map(Keyword::getId).toArray(String[]::new);
    }

    @Override
    public @NotNull KeywordList listKeyword(Integer reqUser, int type, Integer summoner, Integer user, int page) {
        //TODO admin check

        val lType = ListType.get(type);
        if (lType == null)
            return KeywordList.EMPTY;

        val pr = PageRequest.of(page, 5);
        Page<Keyword> keywords;
        if (summoner == null) {
            if (user == null) {
                keywords = switch (lType) {
                    case ALL -> keywordDao.findBy(pr);
                    case USED, UNUSED -> keywordDao.findByUsedEquals(lType == ListType.USED, pr);
                };
            } else {
                keywords = switch (lType) {
                    case ALL -> keywordDao.findByUserIDEquals(user, pr);
                    case USED, UNUSED -> keywordDao.findByUserIDEqualsAndUsedEquals(user, lType == ListType.USED, pr);
                };
            }
        } else {
            if (user == null) {
                keywords = switch (lType) {
                    case ALL -> keywordDao.findByGenerateUserIDEquals(summoner, pr);
                    case USED, UNUSED -> keywordDao.findByGenerateUserIDEqualsAndUsedEquals(summoner,
                        lType == ListType.USED, pr);
                };
            } else {
                keywords = switch (lType) {
                    case ALL -> keywordDao.findByGenerateUserIDEqualsAndUserIDEquals(summoner, user, pr);
                    case USED, UNUSED -> keywordDao.findByGenerateUserIDEqualsAndUserIDEqualsAndUsedEquals(summoner,
                        user, lType == ListType.USED, pr);
                };
            }
        }

        return KeywordList.valueOf(keywords);
    }

    /**
     * 生成token
     *
     * @param user  用户
     * @param index 生成编号
     * @return token
     */
    private @NotNull String summonToken(int user, int index) {
        val t = Long.toUnsignedString(System.currentTimeMillis(), Character.MAX_RADIX);
        val u = Integer.toUnsignedString(user, Character.MAX_RADIX);
        val i = Integer.toUnsignedString(index, Character.MAX_RADIX);
        val r1 = Long.toUnsignedString(random.nextLong(), Character.MAX_RADIX);
        val r2 = Long.toUnsignedString(random.nextLong(), Character.MAX_RADIX);
        val r3 = Long.toUnsignedString(random.nextLong(), Character.MAX_RADIX);
        StringBuilder sb = new StringBuilder();
        sb.append(u).append(i).append(r1).append(r2).append(r3).append(t);
        while (sb.length() < TOKEN_LEN)
            sb.append(Integer.toUnsignedString(random.nextInt(Character.MAX_RADIX), Character.MAX_RADIX));

        return sb.substring(0, TOKEN_LEN);
    }

    /**
     * 列出类型
     */
    private enum ListType {
        ALL, USED, UNUSED;
        private static final ListType[] all = values();

        @Nullable
        @Contract(pure = true)
        public static ListType get(int type) {
            return type < 0 || type >= all.length ? null : all[type];
        }
    }
}
