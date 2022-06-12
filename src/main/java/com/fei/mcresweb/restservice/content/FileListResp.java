package com.fei.mcresweb.restservice.content;

import com.fei.mcresweb.dao.EssayFileInfo;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * 文件列表响应
 *
 * @param success 是否成功
 * @param err     失败的错误消息
 * @param files   文件列表
 */
public record FileListResp(boolean success, String err, Collection<FileInfo> files) {

    @Contract("_ -> new")
    public static @NotNull FileListResp byErr(@NonNull String err) {
        return new FileListResp(false, err, null);
    }

    @Contract("_ -> new")
    public static @NotNull FileListResp byInfoList(@NonNull Collection<FileInfo> files) {
        for (var x : files)
            if (x == null)
                throw new NullPointerException("Contains null element");
        return new FileListResp(true, null, files);
    }

    @Contract("_ -> new")
    public static @NotNull FileListResp byDbList(@NonNull Collection<EssayFileInfo> files) {
        return byInfoList(files.stream().map(FileInfo::new).toList());
    }
}
