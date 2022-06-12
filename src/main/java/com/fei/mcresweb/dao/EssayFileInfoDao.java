package com.fei.mcresweb.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

/**
 * 帖子文件信息
 */
public interface EssayFileInfoDao extends CrudRepository<EssayFileInfo, EssayFileInfoPK> {
    Collection<EssayFileInfo> findAllByEssayIdEquals(int essay);
}
