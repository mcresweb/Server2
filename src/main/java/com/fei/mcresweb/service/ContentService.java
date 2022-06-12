package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.restservice.content.*;
import lombok.NonNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * 内容服务
 */
public interface ContentService {
    /**
     * 列出大分类
     *
     * @return 大分类列表
     */
    @NonNull Collection<CatalogueInfo> listCatalogue();

    /**
     * 列出小分类
     *
     * @param catalogue 大分类
     * @return 小分类列表
     */
    @Nullable
    Collection<CategoryInfo> listCategory(String catalogue);

    /**
     * 列出内容
     *
     * @param catalogue 大分类
     * @param category  小分类
     * @param page      页码
     * @return 内容列表
     */
    @Nullable
    EssayList listEssay(String catalogue, String category, int page);

    /**
     * 获取内容详细信息
     *
     * @param id 内容ID
     * @return 详细信息
     */
    @Nullable
    EssayDetail essay(int id);

    /**
     * 修改大分类
     *
     * @param catalogue 大分类信息
     * @return 修改结果
     */
    @NonNull ModResp modCatalogue(@NonNull ModCatalogue catalogue);

    /**
     * 修改小分类
     *
     * @param category 小分类信息
     * @return 修改结果
     */
    @NonNull ModResp modCategory(@NonNull ModCategory category);

    /**
     * 上传内容
     *
     * @param user  上传者
     * @param essay 内容
     * @return 上传结果
     */
    @NonNull UploadResp<Integer> uploadEssay(Integer user, UploadEssay essay);

    /**
     * 随机返回一个内容id
     *
     * @return 内容id
     */
    @Nullable
    Integer randomEssayId();

    /**
     * 上传资源文件
     *
     * @param user  上传者
     * @param id    内容ID
     * @param files 文件
     * @return 上传结果
     */
    UploadResp<Collection<UUID>> uploadFile(Integer user, int id, MultipartFile[] files);

    /**
     * 列出文件列表
     *
     * @param user  查询者
     * @param essay 内容ID
     * @return 文件列表
     */
    @NonNull FileListResp listFile(Integer user, int essay);

    /**
     * 获取资源文件
     *
     * @param essay 内容ID
     * @param file  文件ID
     * @return 文件资源
     */
    @Nullable
    FileSystemResource getFile(int essay, UUID file);

    /**
     * 获取资源文件信息
     *
     * @param essay 内容ID
     * @param file  文件ID
     * @return 文件信息
     */
    @Nullable
    FileInfo getFileInfo(int essay, UUID file);

    /**
     * @param key   唯一标识
     * @param name  名称
     * @param index 排序用ID
     * @param img   展示图片
     */
    record ModCatalogue(@NonNull String key, String name, Double index, UUID img) {
        /**
         * 判断请求是否是 增加或更新 操作
         *
         * @return 是否是C/U操作
         */
        public boolean isCU() {
            return Tool.valid(key) && Tool.valid(name) && index != null && Double.isFinite(index);
        }

        /**
         * 判断请求是否是 删除 操作
         *
         * @return 是否是D操作
         */
        public boolean isD() {
            return Tool.valid(key) && !Tool.valid(name) && index == null;
        }
    }

    /**
     * @param catalogue 所属的大分类
     * @param key       唯一标识
     * @param name      名称
     * @param index     排序用ID
     * @param img       展示图片
     */
    record ModCategory(@NonNull String catalogue, @NonNull String key, String name, Double index, UUID img) {
        /**
         * 判断请求是否是 增加或更新 操作
         *
         * @return 是否是C/U操作
         */
        public boolean isCU() {
            return Tool.valid(catalogue) && Tool.valid(key) && Tool.valid(name) && index != null && Double.isFinite(
                index);
        }

        /**
         * 判断请求是否是 删除 操作
         *
         * @return 是否是D操作
         */
        public boolean isD() {
            return Tool.valid(catalogue) && Tool.valid(key) && !Tool.valid(name) && index == null;
        }
    }

    /**
     * 上传文字
     *
     * @param catalogue   大分类
     * @param category    小分类
     * @param title       内容标题
     * @param imgs        帖子图片
     * @param content     内容文章
     * @param type        内容文章的类型
     * @param description 内容描述
     * @param tags        标签
     */
    record UploadEssay(@NonNull String catalogue, @NonNull String category, @NonNull String title,
                       Map<UUID, ImgUsing> imgs, @NonNull String content, @NonNull String type, String description,
                       Collection<String> tags) {
    }

    /**
     * 使用方式
     *
     * @param head 是否显示在头部
     * @param list 是否显示在内容列表
     */
    record ImgUsing(boolean head, boolean list) {
    }
}
