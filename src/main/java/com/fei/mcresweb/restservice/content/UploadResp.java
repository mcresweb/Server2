package com.fei.mcresweb.restservice.content;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 上传响应
 *
 * @param success 是否成功
 * @param err     错误消息
 * @param id      上传的ID
 * @param <T>     ID类型
 */
public record UploadResp<T>(boolean success, String err, T id) {

    /**
     * 通过错误构造上传响应
     *
     * @param err 错误
     * @return 上传响应
     */
    @Contract("_ -> new")
    public static @NotNull <T> UploadResp<T> byErr(@NonNull String err) {
        return new UploadResp<>(false, err, null);
    }

    /**
     * 通过错误构造上传响应
     *
     * @param id 上传的ID
     * @return 上传响应
     */
    @Contract("_ -> new")
    public static @NotNull <T> UploadResp<T> byId(@NonNull T id) {
        return new UploadResp<>(true, null, id);
    }
}
