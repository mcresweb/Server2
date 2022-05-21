package com.fei.mcresweb.service;

import com.fei.mcresweb.restservice.keyword.KeywordList;
import org.jetbrains.annotations.NotNull;

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
     * 生成请求
     *
     * @param amount 生成的数量
     * @param value  每个激活码的价值（VIP天数）
     * @param expire 失效时间
     */
    record SummonReq(int amount, int value, Long expire) {
    }

}
