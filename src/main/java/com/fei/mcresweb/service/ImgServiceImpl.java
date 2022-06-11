package com.fei.mcresweb.service;

import com.fei.mcresweb.dao.Img;
import com.fei.mcresweb.dao.ImgDao;
import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.dao.UserDao;
import com.fei.mcresweb.restservice.img.UploadResp;
import lombok.NonNull;
import lombok.val;
import org.hibernate.engine.jdbc.BlobProxy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
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
    public @NotNull UploadResp uploadImg(Integer user, @NonNull InputStream file, long length) throws IOException {
        if (user == null)
            return UploadResp.byErr(NOT_LOGIN);
        if (!userDao.findById(user).map(User::isAdmin).orElse(false))
            return UploadResp.byErr(NOT_ADMIN);

        val raw = Files.createTempFile("MRW", ".tmp");
        val thu = Files.createTempFile("MRW", ".tmp");
        UUID uuid;
        try {
            val imgData = ImageIO.read(file);
            ImageIO.write(imgData, "webp", Files.newOutputStream(raw));
            synchronized (createLock) {
                Integer insert;
                do {
                    insert = imgDao.insertUUID(uuid = UUID.randomUUID());
                } while (insert == null || insert < 1);
            }

            val img = new Img();
            img.setId(uuid);
            img.setImg(BlobProxy.generateProxy(Files.newInputStream(raw), Files.size(raw)));
            imgDao.save(img);
        } finally {
            try {
                Files.delete(raw);
            } catch (Throwable ignore) {
            }
            try {
                Files.delete(thu);
            } catch (Throwable ignore) {
            }
        }

        return UploadResp.byUuid(uuid);
    }

    @Override
    public Blob getImg(UUID uuid) {
        return imgDao.findById(uuid).map(Img::getImg).orElse(null);
    }

}
