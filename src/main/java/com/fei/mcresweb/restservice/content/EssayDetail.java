package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.Essay;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * 内容详细信息
 *
 * @param id           内容ID
 * @param catalogueKey 大分类
 * @param categoryKey  小分类
 * @param sender       发布者ID
 * @param title        内容标题
 * @param star         评分
 * @param starAmount   评分人数
 * @param download     下载数
 * @param img          图片
 * @param content      文章
 * @param type         类型
 * @param tags         标签
 * @param description  简略描述
 */
public record EssayDetail(int id, @NonNull String catalogueKey, @NonNull String categoryKey, int sender,
                          @NonNull String title, Double star, long starAmount, long download,
                          @NonNull Collection<UUID> img, @NonNull String content, @NonNull String type,
                          Collection<String> tags, String description) {
    public EssayDetail(@NonNull Essay essay) {
        this(essay.getId(), essay.getCatalogueKey(), essay.getCategoryKey(), essay.getSenderID(), essay.getTitle(),
            essay.getStar(), essay.getStarAmount(), essay.getDownload(), essay.getImg(), essay.getContent(),
            essay.getType(), essay.getTags() == null ? null : Arrays.asList(essay.getTags().split(",")),
            essay.getDescription());
    }
}
