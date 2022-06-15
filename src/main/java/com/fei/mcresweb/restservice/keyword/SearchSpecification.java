package com.fei.mcresweb.restservice.keyword;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.dao.Keyword;
import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.service.KeywordService;
import com.fei.mcresweb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;

/**
 * 搜索条件
 */
@RequiredArgsConstructor
public class SearchSpecification implements Specification<Keyword> {
    private final @NotNull KeywordService.SearchReq req;
    private final @NotNull UserService userService;

    @Override
    public Predicate toPredicate(@NotNull Root<Keyword> kwRoot, @NotNull CriteriaQuery<?> cq,
        @NotNull CriteriaBuilder cb) {
        val predicates = new ArrayList<Predicate>();

        if (req.used() != null)
            predicates.add(cb.equal(kwRoot.get(Keyword.Fields.used), req.used()));

        if (Tool.valid(req.summoner()))
            predicates.add(hasUserEqual(cb, kwRoot, req.summoner(), Keyword.Fields.generateUserID));

        if (Tool.valid(req.user()))
            predicates.add(hasUserEqual(cb, kwRoot, req.user(), Keyword.Fields.userID));

        if (req.summonTime() != null)
            predicates.add(hasTimeInRange(cb, kwRoot, req.summonTime(), Keyword.Fields.generateTime));

        if (req.expireTime() != null)
            predicates.add(hasTimeInRange(cb, kwRoot, req.expireTime(), Keyword.Fields.expire));

        if (req.useTime() != null)
            predicates.add(hasTimeInRange(cb, kwRoot, req.useTime(), Keyword.Fields.useTime));

        return cb.and(predicates.toArray(Predicate[]::new));
    }

    /**
     * 生成时间范围比较语句
     *
     * @param input 用户输入
     * @param field kw表内时间column
     */
    @NotNull
    private Predicate hasTimeInRange(@NotNull CriteriaBuilder cb, @NotNull Root<Keyword> kwRoot, Long @NotNull [] input,
        @NotNull String field) {
        val path = kwRoot.<Date>get(field);
        if (input[0] == null) {// x < R
            return cb.lessThanOrEqualTo(path, new Date(input[1]));
        } else if (input[1] == null) {// L < x
            return cb.greaterThanOrEqualTo(path, new Date(input[0]));
        } else {// L < x < R
            return cb.between(path, new Date(input[0]), new Date(input[1]));
        }
    }

    /**
     * 生成用户信息比较语句
     *
     * @param input 用户输入
     * @param field kw表内用户column
     */
    @NotNull
    private Predicate hasUserEqual(@NotNull CriteriaBuilder cb, @NotNull Root<Keyword> kwRoot, @NotNull String input,
        @NotNull String field) {
        if (userService.maybeUserIdString(input)) {//userID
            return cb.equal(kwRoot.get(field), input);
        }

        val sj = "%" + input.replace(' ', '%') + "%";

        val join = kwRoot.join(Tool.tableName(User.class));
        
        join.on(cb.equal(kwRoot.get(field), join.get(User.Fields.id)));

        if (userService.maybeUserEmailString(input)) {//email
            return cb.like(join.get(User.Fields.email), sj);
        } else {//username
            return cb.like(join.get(User.Fields.username), sj);
        }
    }
}
