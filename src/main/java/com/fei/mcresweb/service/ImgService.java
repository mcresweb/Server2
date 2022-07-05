package com.fei.mcresweb.service;

import com.fei.mcresweb.restservice.img.UploadResp;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.UUID;

/**
 * 图片服务
 */
public interface ImgService {
    /**
     * 上传图片
     *
     * @param user 上传用户
     * @param file 上传的文件
     * @return 上传结果
     */
    @NotNull UploadResp uploadImg(Integer user, @NonNull InputStream file, long length) throws IOException;

    /**
     * 获取图片数据(原始)
     *
     * @param uuid 图片UUID
     * @return 图片数据
     */
    @Nullable Blob getImg(UUID uuid);
}
