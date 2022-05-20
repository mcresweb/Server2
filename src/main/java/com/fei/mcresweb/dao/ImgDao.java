package com.fei.mcresweb.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * 图片
 */
public interface ImgDao extends CrudRepository<Img, UUID> {
}
