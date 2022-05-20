package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.Category;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 小分类信息
 *
 * @param key   小分类唯一标识
 * @param index 小分类排序
 * @param title 小分类标题
 * @param img   小分类的图片
 */
public record CategoryInfo(@NonNull String key, double index, @NonNull String title, UUID img) {
    /**
     * 从数据库构建
     *
     * @param data 数据库返回值
     * @return 小分类信息
     */
    @Contract("_ -> new")
    public static @NotNull CategoryInfo fromDatabase(@NonNull Category data) {
        return new CategoryInfo(data.getKey(), data.getIndex(), data.getTitle(),
            data.getImgID() == null ? null : data.getImgID());
    }
}
