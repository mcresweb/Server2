package com.fei.mcresweb.dao;

import com.fei.mcresweb.Tool;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 帖子文件存储
 */
@Component
public class EssayFileDao {
    public static final int chunkBit = 11;
    /**
     * 单文件夹存储数量
     */
    public static final int chunkAmount = 1 << chunkBit;
    /**
     * 文件夹层数
     */
    public static final int chunkDeep = (int)Math.ceil(Math.log(Integer.MAX_VALUE) / Math.log(chunkAmount));
    /**
     * 文件路径缓存
     */
    private final Map<EssayFileInfoPK, Path> cache = new LinkedHashMap<>(30, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<EssayFileInfoPK, Path> eldest) {
            return size() > 20;
        }
    };

    public static final Path dir = Path.of("runtime", "essay-files").toAbsolutePath();

    public EssayFileDao() {
        if (Files.exists(dir)) {
            if (!Files.isDirectory(dir))
                throw new DataAccessResourceFailureException("Bad resource dir: " + dir);
        } else {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new PermissionDeniedDataAccessException("Can not create resource dir: " + dir, e);
            }
        }
    }

    /**
     * 保存资源文件
     *
     * @param key  文件键
     * @param file 文件主体
     */
    public void saveFile(@NonNull EssayFileInfoPK key, @NonNull MultipartFile file) {
        val path = getFile(key);
        if (Files.exists(path))
            throw new DataIntegrityViolationException("File exists: " + path);
        try {
            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new DataAccessResourceFailureException("Can not write File: " + path, e);
        }
    }

    /**
     * 获取文件资源
     *
     * @param key 文件键
     * @return 文件资源
     */
    @Nullable
    public Path getFileResource(@NonNull EssayFileInfoPK key) {
        val path = getFile(key);
        if (!Files.exists(path))
            return null;
        if (!Files.isRegularFile(path))
            throw new DataAccessResourceFailureException("Can not found file: " + path);
        return path;
    }

    /**
     * 获取文件大小
     *
     * @param key 文件键
     * @return 文件大小 (bytes)
     */
    public long getFileSize(@NonNull EssayFileInfoPK key) {
        val path = getFile(key);
        if (!Files.isRegularFile(path))
            throw new DataAccessResourceFailureException("Can not found file: " + path);
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new DataAccessResourceFailureException("Can not write File: " + path, e);
        }
    }

    /**
     * 获取资源文件的sha1摘要
     *
     * @param key 文件键
     * @return sha1摘要
     */
    public String getFileSha1(@NonNull EssayFileInfoPK key) {
        val path = getFile(key);
        if (!Files.isRegularFile(path))
            throw new DataRetrievalFailureException("Can not found file: " + path);
        try (val in = Files.newInputStream(path)) {
            byte[] buf = new byte[4096];
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            int len;
            while ((len = in.read(buf)) > 0)
                digest.update(buf, 0, len);
            return Tool.byte2hex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError(e);
        } catch (IOException e) {
            throw new DataAccessResourceFailureException("Can not read File: " + path, e);
        }
    }

    /**
     * 获取文件路径
     *
     * @param key 文件键
     * @return 文件路径
     */
    private @NotNull Path getFile(@NonNull EssayFileInfoPK key) {
        Path path = cache.get(key);
        if (path != null)
            return path;

        int essay = key.getEssayId();
        String[] arr = new String[chunkDeep];//essay[1] essay[2] file

        for (int i = chunkDeep - 2; i >= 0; i--) {
            arr[i] = Integer.toString(essay % chunkAmount);
            essay = essay >>> chunkBit;
        }
        arr[chunkDeep - 1] = key.getFileId().toString();

        path = dir.resolve(Path.of(Integer.toString(essay), arr));

        try {
            if (!Files.exists(path))
                Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new PermissionDeniedDataAccessException("Can not create resource dir: " + dir, e);
        }

        cache.put(key, path);
        return path;
    }

}
