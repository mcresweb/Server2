package com.fei.mcresweb.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * 会员码
 */
public interface KeywordDao extends CrudRepository<Keyword, String> {

    Page<Keyword> findByGenerateUserIDEqualsAndUserIDEquals(int generateUserID, int userID, Pageable pageable);

    Page<Keyword> findByUserIDEquals(int userID, Pageable pageable);

    Page<Keyword> findByGenerateUserIDEquals(int generateUserID, Pageable pageable);

    Page<Keyword> findBy(Pageable pageable);

    Page<Keyword> findByGenerateUserIDEqualsAndUserIDEqualsAndUsedEquals(int generateUserID, int userID, boolean used,
        Pageable pageable);

    Page<Keyword> findByUserIDEqualsAndUsedEquals(int userID, boolean used, Pageable pageable);

    Page<Keyword> findByGenerateUserIDEqualsAndUsedEquals(int generateUserID, boolean used, Pageable pageable);

    Page<Keyword> findByUsedEquals(boolean used, Pageable pageable);
    
}
