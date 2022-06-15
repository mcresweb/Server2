package com.fei.mcresweb.service;

import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.dao.Keyword;
import com.fei.mcresweb.dao.KeywordDao;
import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.dao.UserDao;
import com.fei.mcresweb.defs.TokenHelper;
import com.fei.mcresweb.restservice.keyword.KeywordList;
import com.fei.mcresweb.restservice.keyword.RemoveResp;
import com.fei.mcresweb.restservice.keyword.SearchSpecification;
import com.fei.mcresweb.restservice.keyword.UseResult;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * 会员码服务
 */
@Service
public class KeywordServiceImpl implements KeywordService {
    /**
     * TOKEN长度
     */
    public static final int TOKEN_LEN = TokenHelper.TOKEN_LEN;
    private final Random random = new SecureRandom();
    private final KeywordDao keywordDao;
    private final UserDao userDao;

    private final UserService userService;

    private static final String BAD_TOKEN = "无效token";

    public KeywordServiceImpl(KeywordDao keywordDao, UserDao userDao, UserService userService) {
        this.keywordDao = keywordDao;
        this.userDao = userDao;
        this.userService = userService;
    }

    @Override
    public String[] summon(Integer user, @NotNull SummonReq req) {
        if (user == null)
            return null;
        if (!userDao.findById(user).map(User::isAdmin).orElse(false))
            return null;

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
    public @NotNull KeywordList listKeyword(Integer reqUser, SearchReq req) {
        if (reqUser == null)
            return KeywordList.EMPTY;
        if (!userDao.findById(reqUser).map(User::isAdmin).orElse(false))
            return KeywordList.EMPTY;

        val pr = PageRequest.of(req.page() - 1, 10);
        Page<Keyword> keywords = keywordDao.findAll(new SearchSpecification(req, userService), pr);

        return KeywordList.valueOf(keywords);
    }

    @Override
    public synchronized @NotNull UseResult useKeyword(@NotNull Locale locale, Integer userID, UseReq body) {
        val user = getUser(userID);

        val kw = keywordDao.findById(body.token()).orElse(null);
        if (kw == null || kw.isUsed() || kw.isExpire())
            return new UseResult(I18n.msg("bad-token", locale));

        kw.use(userID);
        keywordDao.save(kw);

        val expire = user.addVip(kw.getValue());
        userDao.save(user);

        return new UseResult(expire);
    }

    @Override
    public synchronized @NotNull RemoveResp removeKeyword(Integer userID, RemoveReq body) {
        //有效的token
        val valid = StreamSupport.stream(keywordDao.findAllById(body.tokens()).spliterator(), true)//
            .filter(kw -> !kw.isUsed())//
            .map(Keyword::getId)//
            .toList();

        if (valid.size() != body.tokens().size()) {
            val all = new HashSet<>(body.tokens());
            valid.forEach(all::remove);
            return RemoveResp.byErr(BAD_TOKEN, all);
        }

        keywordDao.deleteAllById(valid);

        return RemoveResp.SUCCESS;
    }

    /**
     * 获取用户
     *
     * @param userID 用户ID
     * @return 用户
     * @throws IllegalArgumentException 找不到用户
     */
    private User getUser(int userID) {
        return userDao.findById(userID).orElseThrow(() -> new IllegalArgumentException("Not Found User"));
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

        return sb.substring(0, TOKEN_LEN).toUpperCase(Locale.ENGLISH);
    }

}
