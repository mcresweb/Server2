package com.fei.mcresweb.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 会员码
 */
public interface KeywordDao extends CrudRepository<Keyword, String>, JpaSpecificationExecutor<Keyword> {

}
