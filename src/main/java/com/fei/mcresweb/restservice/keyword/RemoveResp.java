package com.fei.mcresweb.restservice.keyword;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 移除响应
 *
 * @param success 是否成功
 * @param err     错误消息
 */
public record RemoveResp(boolean success, String err, @Nullable List<String> badToken) {
    /**
     * 成功响应
     */
    public static final RemoveResp SUCCESS = new RemoveResp(true, null, null);

    /**
     * 通过错误构造移除响应
     *
     * @param err 错误
     * @return 移除响应
     */
    @Contract("_,_ -> new")
    public static @NotNull RemoveResp byErr(@NonNull String err, @Nullable Collection<String> badToken) {
        return new RemoveResp(false, err, badToken == null || badToken.isEmpty() ? null : new ArrayList<>(badToken));
    }
}
