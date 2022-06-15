package com.fei.mcresweb.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 用户
 */
public interface UserDao extends CrudRepository<User, Integer> {

    User findByUsername(String name);

    /**
     * @return 是否存在管理员用户
     */
    @Query(value = "select 1 from user where user.is_admin = 1", nativeQuery = true)
    Integer anyAdmin();
}
