package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.EssayRecommend;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 内容推荐列表
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EssayRecommendList {
    /**
     * 页数
     */
    long page;
    /**
     * 内容推荐列表
     */
    @NonNull List<EssayRecommendInfo> list;

    /**
     * 构建一个内容推荐列表
     *
     * @param page 页数
     * @param list 内容推荐列表
     * @return 内容推荐列表
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull EssayRecommendList build(long page, @NonNull Collection<EssayRecommendInfo> list) {
        return new EssayRecommendList(page, new ArrayList<>(list));
    }

    /**
     * 内容推荐信息
     *
     * @param id          内容ID
     * @param sender      发布者用户名
     * @param title       内容标题
     * @param star        评分
     * @param download    下载数
     * @param img         内容图片
     * @param expire      过期时间
     * @param hoist       提升时间
     * @param recommender 推荐者用户名
     */
    public record EssayRecommendInfo(int id, @NonNull String sender, @NonNull String title, Double star, long download,
                                     UUID img, long expire, long hoist, String recommender) {

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            EssayRecommendInfo essayInfo = (EssayRecommendInfo)o;
            return id == essayInfo.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Contract(value = "_ -> new", pure = true)
        public static @NotNull EssayRecommendInfo valueOf(@NonNull EssayRecommend essayRecommend) {
            val essay = essayRecommend.getEssay();
            return new EssayRecommendInfo(essay.getId(), essay.getSender().getUsername(), essay.getTitle(),
                essay.getStar(), essay.getDownload(), essay.getAnyListImg(), essayRecommend.getExpire().getTime(),
                essayRecommend.getHoist(), essayRecommend.getSender().getUsername());
        }

    }

}
