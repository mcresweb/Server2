package com.fei.mcresweb.service;

import com.fei.mcresweb.restservice.img.UploadResp;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    @NotNull UploadResp uploadImg(Integer user, @NonNull MultipartFile file) throws IOException;
}
