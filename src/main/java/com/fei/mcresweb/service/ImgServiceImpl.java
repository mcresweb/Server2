package com.fei.mcresweb.service;

import com.fei.mcresweb.dao.ImgDao;
import com.fei.mcresweb.dao.UserDao;
import com.fei.mcresweb.restservice.img.UploadResp;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;

/**
 * 图片服务
 */
@Service
public class ImgServiceImpl implements ImgService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private ImgDao imgDao;

    private static final String NOT_LOGIN = "未登录";
    private static final String NOT_ADMIN = "非管理员";

    private static final Object createLock = new Object();

    @Override
    @Transactional
    public @NotNull UploadResp uploadImg(Integer user, @NonNull MultipartFile file) throws IOException {
        //        if (user == null)
        //            return UploadResp.byErr(NOT_LOGIN);
        //TODO admin check

        UUID uuid;
        synchronized (createLock) {
            Integer insert;
            do {
                insert = imgDao.insertUUID(uuid = UUID.randomUUID());
            } while (insert == null || insert < 1);
        }
        //        val tmp = Files.createTempFile("MRW", ".tmp");
        //        val raw = Files.createTempFile("MRW", ".tmp");
        //        val thu = Files.createTempFile("MRW", ".tmp");
        //        file.transferTo(tmp.toFile());

        imgDao.uploadRawImg(uuid, file.getInputStream());

        //TODO

        return UploadResp.byUuid(uuid);
    }
}
