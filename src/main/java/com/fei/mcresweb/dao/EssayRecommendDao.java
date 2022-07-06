package com.fei.mcresweb.dao;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * 内容推荐数据
 */
public interface EssayRecommendDao extends JpaRepository<EssayRecommend, Integer> {

    @NotNull Page<EssayRecommend> findAll(@NotNull Pageable pageable);

    @NotNull Page<EssayRecommend> findByExpireBeforeOrderByHoistDesc(@NotNull Date expire, @NotNull Pageable pageable);
}
