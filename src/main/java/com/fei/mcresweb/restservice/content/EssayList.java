package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.Essay;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 内容列表
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EssayList {
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
    @Contract("_, _ -> new")
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
                essay.getAnyImg());
        }
    }

}
