package com.fei.mcresweb.dao;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

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
}
