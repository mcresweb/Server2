package com.fei.mcresweb.dao;

import org.springframework.data.repository.CrudRepository;

/**
 * 用户
 */
public interface UserDao extends CrudRepository<User, Integer> {

    User findByUsername(String name);
}
