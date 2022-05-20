package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.Catalogue;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 大分类信息
 *
 * @param key   大分类唯一标识
 * @param index 大分类排序
 * @param title 大分类标题
 * @param img   大分类的图片
 */
public record CatalogueInfo(@NonNull String key, double index, @NonNull String title, UUID img) {
    /**
     * 从数据库构建
     *
     * @param data 数据库返回值
     * @return 大分类信息
     */
    @Contract("_ -> new")
    public static @NotNull CatalogueInfo fromDatabase(@NonNull Catalogue data) {
        return new CatalogueInfo(data.getKey(), data.getIndex(), data.getTitle(),
            data.getImgID() == null ? null : data.getImgID());
    }

}
