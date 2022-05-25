package com.fei.mcresweb.restservice.img;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 上传响应
 *
 * @param success 是否成功
 * @param err     错误消息
 * @param uuid    UUID
 */
public record UploadResp(boolean success, String err, UUID uuid) {
    @Contract("_ -> new")
    public static @NotNull UploadResp byErr(@NonNull String err) {
        return new UploadResp(false, err, null);
    }

    @Contract("_ -> new")
    public static @NotNull UploadResp byUuid(@NonNull UUID uuid) {
        return new UploadResp(true, null, uuid);
    }
}
