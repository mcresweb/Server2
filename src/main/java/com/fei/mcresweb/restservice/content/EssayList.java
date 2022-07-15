package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.Essay;
import com.fei.mcresweb.dao.EssayRecommend;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 内容列表
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EssayList {
    /**
     * 空的内容列表
     */
    public static final EssayList EMPTY = new EssayList(0, Collections.emptyList());
    /**
     * 页数
     */
    long page;
    /**
     * 内容列表
     */
    @NonNull List<EssayInfo> list;

    /**
     * 构建一个内容列表
     *
     * @param page 页数
     * @param list 内容列表
     * @return 内容列表
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull EssayList build(long page, @NonNull Collection<EssayInfo> list) {
        return new EssayList(page, new ArrayList<>(list));
    }

    /**
     * 内容信息
     *
     * @param id       内容ID
     * @param sender   发布者用户名
     * @param title    内容标题
     * @param star     评分
     * @param download 下载数
     * @param img      内容图片
     */
    public record EssayInfo(int id, @NonNull String sender, @NonNull String title, Double star, long download,
                            UUID img) {
        public EssayInfo(@NonNull Essay essay) {
            this(essay.getId(), essay.getSender().getUsername(), essay.getTitle(), essay.getStar(), essay.getDownload(),
                essay.getAnyListImg());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            EssayInfo essayInfo = (EssayInfo)o;
            return id == essayInfo.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        public EssayInfo(@NonNull EssayRecommend essayRecommend) {
            this(essayRecommend.getEssay());
        }
    }

}
