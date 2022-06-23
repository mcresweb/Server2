package com.fei.mcresweb.service;

import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.dao.*;
import com.fei.mcresweb.restservice.content.*;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 内容服务
 */
@Service
public class ContentServiceImpl implements ContentService {

    private final CatalogueDao catalogueDao;
    private final CategoryDao categoryDao;
    private final EssayDao essayDao;
    private final EssayImgsDao essayImgsDao;
    private final EssayFileDao essayFileDao;
    private final EssayFileInfoDao essayFileInfoDao;
    private final ContentSearchService contentSearchService;

    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private Map<String, CatalogueInfo> cache_catalogue;
    private Map<String, LinkedHashMap<String, CategoryInfo>> cache_category;

    public ContentServiceImpl(CatalogueDao catalogueDao, CategoryDao categoryDao, EssayDao essayDao,
        EssayImgsDao essayImgsDao, EssayFileDao essayFileDao, EssayFileInfoDao essayFileInfoDao,
        ContentSearchService contentSearchService) {
        this.catalogueDao = catalogueDao;
        this.categoryDao = categoryDao;
        this.essayDao = essayDao;
        this.essayImgsDao = essayImgsDao;
        this.essayFileDao = essayFileDao;
        this.essayFileInfoDao = essayFileInfoDao;
        this.contentSearchService = contentSearchService;

        withCache(() -> 0);
    }

    /**
     * 清除分类缓存
     */
    private void clearCache() {
        cacheLock.writeLock().lock();
        try {
            cache_catalogue = null;
            cache_category = null;
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * 使用分类缓存
     *
     * @param runner 运行体
     * @param <T>    返回参数
     * @return 返回数据
     */
    private <T> T withCache(Supplier<T> runner) {
        cacheLock.readLock().lock();
        try {
            if (cache_catalogue != null)
                return runner.get();
        } finally {
            cacheLock.readLock().unlock();
        }
        cacheLock.writeLock().lock();
        try {
            if (cache_catalogue == null) {
                cache_catalogue = new LinkedHashMap<>();
                cache_category = new LinkedHashMap<>();
                for (val catalogue : catalogueDao.findAll()) {
                    cache_catalogue.put(catalogue.getKey(), CatalogueInfo.fromDatabase(catalogue));
                    val map = new LinkedHashMap<String, CategoryInfo>();
                    cache_category.put(catalogue.getKey(), map);
                    for (val category : catalogue.getCategoryList()) {
                        map.put(category.getKey(), CategoryInfo.fromDatabase(category));
                    }
                }
            }
            return runner.get();
        } finally {
            cacheLock.writeLock().unlock();
        }

    }

    @Override
    public @NonNull Collection<CatalogueInfo> listCatalogue() {
        return withCache(() -> cache_catalogue.values());
    }

    @Override
    public Collection<CategoryInfo> listCategory(String catalogue) {
        if (catalogue == null)
            return null;
        return withCache(() -> {
            val map = cache_category.get(catalogue);
            return map == null ? null : map.values();
        });
    }

    @Override
    public EssayList listEssay(String catalogue, String category, int page) {
        if (catalogue == null || category == null || page < 0 || withCache(
            () -> cache_category.get(catalogue) == null || cache_category.get(catalogue).get(category) == null))
            return null;
        val list = essayDao.findByCatalogueKeyEqualsAndCategoryKeyEquals(catalogue, category, PageRequest.of(page, 5));
        if (list == null || list.getTotalElements() <= 0)
            return null;
        return EssayList.build(list.getTotalPages(),
            list.stream().map(EssayList.EssayInfo::new).collect(Collectors.toList()));
    }

    @Override
    public EssayDetail essay(int id) {
        return essayDao.findById(id).map(EssayDetail::new).orElse(null);
    }

    @Override
    @NonNull
    public ModResp modCatalogue(@Nullable Locale locale, @NonNull ModCatalogue data) {
        try {
            if (data.isCU()) {
                val info = new Catalogue();
                if (data.index() != null)
                    info.setIndex(data.index());
                info.setTitle(data.name());
                info.setKey(data.key());
                info.setImgID(data.img());
                catalogueDao.save(info);
                return ModResp.SUCCESS;
            } else if (data.isD()) {
                try {
                    catalogueDao.deleteById(data.key());
                } catch (DataIntegrityViolationException e) {
                    return ModResp.byErr(I18n.msg("not-empty.catalogue", locale));
                }
                return ModResp.SUCCESS;
            }
        } catch (DataAccessException e) {
            return ModResp.byErr(I18n.msg("bad-data", locale));
        } finally {
            clearCache();
        }
        return ModResp.byErr(I18n.msg("bad-data", locale));
    }

    @Override
    @NonNull
    public ModResp modCategory(@Nullable Locale locale, @NonNull ModCategory data) {
        try {
            if (data.isCU()) {
                if (!withCache(() -> cache_catalogue.containsKey(data.catalogue())))
                    return ModResp.byErr(I18n.msg("notfound.catalogue", locale));
                val info = new Category();
                info.setCatalogueKey(data.catalogue());
                info.setKey(data.key());
                info.setTitle(data.name());
                if (data.index() != null)
                    info.setIndex(data.index());
                info.setImgID(data.img());
                categoryDao.save(info);
                return ModResp.SUCCESS;
            } else if (data.isD()) {
                if (!withCache(() -> cache_catalogue.containsKey(data.catalogue())))
                    return ModResp.byErr(I18n.msg("notfound.catalogue", locale));
                try {
                    categoryDao.deleteById(data.key());
                } catch (DataIntegrityViolationException e) {
                    return ModResp.byErr(I18n.msg("not-empty.category", locale));
                }
                return ModResp.SUCCESS;
            }
        } catch (DataAccessException e) {
            return ModResp.byErr(I18n.msg("bad-data", locale));
        } finally {
            clearCache();
        }
        return ModResp.byErr(I18n.msg("bad-data", locale));
    }

    @Override
    public @NonNull UploadResp<Integer> uploadEssay(@Nullable Locale locale, Integer user, UploadEssay data) {

        if (!withCache(() -> cache_category.containsKey(data.catalogue())))
            return UploadResp.byErr(I18n.msg("notfound.catalogue", locale));
        if (!withCache(() -> cache_category.get(data.catalogue()).containsKey(data.category())))
            return UploadResp.byErr(I18n.msg("notfound.category", locale));
        if (data.tags() != null)
            for (val tag : data.tags())
                if (tag.contains(","))
                    return UploadResp.byErr(I18n.msg("bad-data", locale));

        var essay = new Essay();
        essay.setCatalogueKey(data.catalogue());
        essay.setCategoryKey(data.category());
        essay.setSenderID(user);
        essay.setTitle(data.title());
        essay.setContent(data.content());
        essay.setType(data.type());
        essay.setDescription(data.description());
        essay.setTagsList(data.tags());
        try {
            essay = essayDao.save(essay);
            essay.setImg(data.imgs() == null ? Collections.emptyMap() : data.imgs());
            essayImgsDao.saveAll(essay.getImg());
            try {
                contentSearchService.writeEssay(essay);
            } catch (IOException e) {
                e.printStackTrace();
                return UploadResp.byErr(I18n.msg("search.write.ioe", locale, e));
            }
            return UploadResp.byId(essay.getId());
        } catch (DataAccessException e) {
            return UploadResp.byErr(I18n.msg("bad-data", locale));
        }
    }

    @Override
    @Nullable
    public Integer randomEssayId() {
        return essayDao.getRandomEssay();
    }

    @Override
    public UploadResp<Collection<UUID>> uploadFile(@Nullable Locale locale, Integer user, int id,
        MultipartFile[] files) {

        if (!essayDao.existsById(id))
            return UploadResp.byErr(I18n.msg("bad-data", locale));

        Collection<UUID> uuids = new ArrayList<>();
        for (val file : files) {
            val pk = new EssayFileInfoPK(id, UUID.randomUUID());
            essayFileDao.saveFile(pk, file);
            val info = new EssayFileInfo();
            info.setFileId(pk.getFileId());
            info.setEssayId(id);
            info.setName(file.getOriginalFilename());
            info.setSize(essayFileDao.getFileSize(pk));
            info.setSha1(essayFileDao.getFileSha1(pk));
            info.setUploaderID(user);
            essayFileInfoDao.save(info);
            uuids.add(info.getFileId());
        }

        return UploadResp.byId(uuids);
    }

    @Override
    public @NotNull FileListResp listFile(@Nullable Locale locale, Integer user, int essay) {

        val list = essayFileInfoDao.findAllByEssayIdEquals(essay);
        if (list == null)
            return FileListResp.byErr(I18n.msg("notfound.essay", locale));

        return FileListResp.byDbList(list);
    }

    @Override
    public FileSystemResource getFile(int essay, UUID file) {
        essayDao.addDownload(essay);
        val path = essayFileDao.getFileResource(new EssayFileInfoPK(essay, file));
        return path == null ? null : new FileSystemResource(path);
    }

    @Override
    public FileInfo getFileInfo(int essay, UUID file) {
        val info = essayFileInfoDao.findById(new EssayFileInfoPK(essay, file));
        return info.map(FileInfo::new).orElse(null);
    }

    @Override
    public int countCatalogue() {
        return withCache(() -> cache_catalogue.size());
    }

    @Override
    public long countEssay() {
        return essayDao.count();
    }
}
