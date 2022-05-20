package com.fei.mcresweb.restservice.content;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 操作响应
 *
 * @param success 是否成功
 * @param err     错误消息
 */
public record ModResp(boolean success, String err) {
    /**
     * 成功响应
     */
    public static final ModResp SUCCESS = new ModResp(true, null);

    /**
     * 通过错误构造操作响应
     *
     * @param err 错误
     * @return 操作响应
     */
    @Contract("_ -> new")
    public static @NotNull ModResp byErr(@NonNull String err) {
        return new ModResp(false, err);
    }
}
