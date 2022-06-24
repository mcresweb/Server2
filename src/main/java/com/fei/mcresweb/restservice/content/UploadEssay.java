package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.Essay;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * 上传文字
 *
 * @param catalogue   大分类
 * @param category    小分类
 * @param title       内容标题
 * @param imgs        帖子图片
 * @param content     内容文章
 * @param type        内容文章的类型
 * @param description 内容描述
 * @param tags        标签
 */
public record UploadEssay(@NonNull String catalogue, @NonNull String category, @NonNull String title,
                          Map<UUID, ImgUsing> imgs, @NonNull String content, @NonNull String type, String description,
                          Collection<String> tags) {
    public UploadEssay(@NonNull Essay essay) {
        this(essay.getCatalogueKey(), essay.getCategoryKey(), essay.getTitle(), essay.getImgUsing(), essay.getContent(),
            essay.getType(), essay.getDescription(), essay.getTagsList());
    }
}