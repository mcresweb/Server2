package com.fei.mcresweb.dao;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import javax.transaction.Transactional;

/**
 * 内容
 */
public interface EssayDao extends CrudRepository<Essay, Integer> {
    /**
     * 通过类别查找内容列表
     *
     * @param catalogueKey 大分类
     * @param categoryKey  小分类
     * @param pageable     页码
     * @return 查询结果
     */
    Page<Essay> findByCatalogueKeyEqualsAndCategoryKeyEquals(@NonNull String catalogueKey, @NonNull String categoryKey,
        Pageable pageable);

    /**
     * 随机获取一个内容ID
     *
     * @return essay id
     */
    @Query(value = "SELECT`id`FROM`essay`WHERE`id`>=rand()*(SELECT max(id)FROM`essay`)LIMIT 1;", nativeQuery = true)
    @Nullable
    Integer getRandomEssay();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE`essay`SET download=download+1 WHERE id=?1", nativeQuery = true)
    void addDownload(int essay);
}
