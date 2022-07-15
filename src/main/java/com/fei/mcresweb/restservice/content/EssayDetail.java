package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.Essay;
import com.fei.mcresweb.dao.EssayImgs;
import lombok.NonNull;

import java.util.Collection;
import java.util.UUID;

/**
 * 内容详细信息
 *
 * @param id          内容ID
 * @param catalogue   大分类
 * @param category    小分类
 * @param sender      发布者ID
 * @param title       内容标题
 * @param star        评分
 * @param starAmount  评分人数
 * @param download    下载数
 * @param imgs        图片
 * @param content     文章
 * @param type        类型
 * @param tags        标签
 * @param description 简略描述
 * @param files       资源文件数量
 */
public record EssayDetail(int id, @NonNull String catalogue, @NonNull String category, int sender,
                          @NonNull String title, Double star, long starAmount, long download,
                          @NonNull Collection<UUID> imgs, @NonNull String content, @NonNull String type,
                          Collection<String> tags, String description, int files) {
    public EssayDetail(@NonNull Essay essay) {
        this(essay.getId(), essay.getCatalogueKey(), essay.getCategoryKey(), essay.getSenderID(), essay.getTitle(),
            essay.getStar(), essay.getStarAmount(), essay.getDownload(),
            essay.getImg().stream().filter(EssayImgs::isShowInHead).map(EssayImgs::getImgId).toList(),
            essay.getContent(), essay.getType(), essay.getTagsList(), essay.getDescription(), essay.getFiles().size());
    }
}
