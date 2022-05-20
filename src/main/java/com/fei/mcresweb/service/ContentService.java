package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.restservice.content.*;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * 内容服务
 */
public interface ContentService {
    /**
     * 列出大分类
     *
     * @return 大分类列表
     */
    @NonNull Collection<CatalogueInfo> listCatalogue();

    /**
     * 列出小分类
     *
     * @param catalogue 大分类
     * @return 小分类列表
     */
    @Nullable
    Collection<CategoryInfo> listCategory(String catalogue);

    /**
     * 列出内容
     *
     * @param catalogue 大分类
     * @param category  小分类
     * @param page      页码
     * @return 内容列表
     */
    @Nullable
    EssayList listEssay(String catalogue, String category, int page);

    /**
     * 获取内容详细信息
     *
     * @param id 内容ID
     * @return 详细信息
     */
    @Nullable
    EssayDetail essay(int id);

    /**
     * 修改大分类
     *
     * @param catalogue 大分类信息
     * @return 修改结果
     */
    ModResp modCatalogue(@NonNull ModCatalogue catalogue);

    /**
     * 修改小分类
     *
     * @param category 小分类信息
     * @return 修改结果
     */
    ModResp modCategory(@NonNull ModCategory category);

    /**
     * @param key   唯一标识
     * @param name  名称
     * @param index 排序用ID
     * @param img   展示图片
     */
    record ModCatalogue(@NonNull String key, String name, Double index, UUID img) {
        /**
         * 判断请求是否是 增加或更新 操作
         *
         * @return 是否是C/U操作
         */
        public boolean isCU() {
            return Tool.valid(key) && Tool.valid(name) && index != null && Double.isFinite(index);
        }

        /**
         * 判断请求是否是 删除 操作
         *
         * @return 是否是D操作
         */
        public boolean isD() {
            return Tool.valid(key) && !Tool.valid(name) && index == null;
        }
    }

    /**
     * @param catalogue 所属的大分类
     * @param key       唯一标识
     * @param name      名称
     * @param index     排序用ID
     * @param img       展示图片
     */
    record ModCategory(@NonNull String catalogue, @NonNull String key, String name, Double index, UUID img) {
        /**
         * 判断请求是否是 增加或更新 操作
         *
         * @return 是否是C/U操作
         */
        public boolean isCU() {
            return Tool.valid(catalogue) && Tool.valid(key) && Tool.valid(name) && index != null && Double.isFinite(
                index);
        }

        /**
         * 判断请求是否是 删除 操作
         *
         * @return 是否是D操作
         */
        public boolean isD() {
            return Tool.valid(catalogue) && Tool.valid(key) && !Tool.valid(name) && index == null;
        }
    }

}
