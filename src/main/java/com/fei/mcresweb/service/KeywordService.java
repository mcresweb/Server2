package com.fei.mcresweb.service;

import com.fei.mcresweb.restservice.keyword.KeywordList;
import com.fei.mcresweb.restservice.keyword.RemoveResp;
import com.fei.mcresweb.restservice.keyword.UseResult;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.fei.mcresweb.defs.TokenHelper.isInvalid;

/**
 * 会员码服务
 */
public interface KeywordService {

    /**
     * 生成会员码
     *
     * @param user 生成用户
     * @param req  生成请求
     * @return 生成的会员码
     */
    String[] summon(Integer user, SummonReq req);

    /**
     * 列出所有的会员码
     *
     * @param reqUser  请求用户
     * @param type     请求类型
     * @param summoner 指定生成者
     * @param user     指定使用者
     * @param page     页码
     * @return 会员码列表
     */
    @NotNull KeywordList listKeyword(Integer reqUser, int type, Integer summoner, Integer user, int page);

    /**
     * 使用会员码
     *
     * @param user 使用的用户
     * @param body 使用的token
     * @return 使用结果
     */
    @NotNull UseResult useKeyword(Integer user, UseReq body);

    /**
     * 移除会员码
     *
     * @param user 请求的用户
     * @param body 移除的token
     * @return 移除结果
     */
    @NotNull RemoveResp removeKeyword(Integer user, RemoveReq body);

    /**
     * 生成请求
     *
     * @param amount 生成的数量
     * @param value  每个激活码的价值（VIP天数）
     * @param expire 失效时间
     */
    record SummonReq(int amount, int value, Long expire) {
    }

    /**
     * 使用请求
     *
     * @param token 使用的会员码
     */
    record UseReq(@NonNull String token) {
        public UseReq(@NonNull String token) {
            this.token = token.toUpperCase(Locale.ENGLISH);
            if (isInvalid(this.token))
                throw new IllegalArgumentException("Bad token");
        }

    }

    /**
     * 移除请求
     *
     * @param tokens 要移除的会员码列表
     */
    record RemoveReq(@NonNull List<String> tokens) {
        public RemoveReq(@NonNull List<String> tokens) {
            this.tokens = new ArrayList<>(tokens.size());
            for (var token : tokens) {
                token = token.toUpperCase(Locale.ENGLISH);
                if (isInvalid(token))
                    throw new IllegalArgumentException("Bad token");
                this.tokens.add(token);
            }
        }
    }

}
