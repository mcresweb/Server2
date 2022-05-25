package com.fei.mcresweb.service;

import com.fei.mcresweb.dao.*;
import com.fei.mcresweb.restservice.content.*;
import lombok.NonNull;
import lombok.val;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 内容服务
 */
@Service
public class ContentServiceImpl implements ContentService {
    private static final String MSG_NOT_FOUND_CATALOGUE = "未找到大分类!";
    private static final String MSG_NOT_FOUND_CATEGORY = "未找到小分类!";
    private static final String MSG_NOT_LOGIN = "未登录";
    private static final String MSG_BAD_DATA = "错误数据";
    private final CatalogueDao catalogueDao;
    private final CategoryDao categoryDao;
    private final EssayDao essayDao;
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private Map<String, CatalogueInfo> cache_catalogue;
    private Map<String, LinkedHashMap<String, CategoryInfo>> cache_category;

    public ContentServiceImpl(CatalogueDao catalogueDao, CategoryDao categoryDao, EssayDao essayDao) {
        this.catalogueDao = catalogueDao;
        this.categoryDao = categoryDao;
        this.essayDao = essayDao;

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
        cacheLock.writeLock().lock();
        try {
            if (cache_catalogue == null) {
                cacheLock.writeLock().lock();
                try {
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
                } finally {
                    cacheLock.writeLock().unlock();
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
    public ModResp modCatalogue(@NonNull ModCatalogue data) {
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
                catalogueDao.deleteById(data.key());
                return ModResp.SUCCESS;
            }
        } catch (DataAccessException e) {
            return ModResp.byErr(MSG_BAD_DATA);
        } finally {
            clearCache();
        }
        return ModResp.byErr(MSG_BAD_DATA);
    }

    @Override
    @NonNull
    public ModResp modCategory(@NonNull ModCategory data) {
        try {
            if (data.isCU()) {
                if (!withCache(() -> cache_catalogue.containsKey(data.catalogue())))
                    return ModResp.byErr(MSG_NOT_FOUND_CATALOGUE);
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
                    return ModResp.byErr(MSG_NOT_FOUND_CATALOGUE);
                categoryDao.deleteById(data.key());
                return ModResp.SUCCESS;
            }
        } catch (DataAccessException e) {
            return ModResp.byErr(MSG_BAD_DATA);
        } finally {
            clearCache();
        }
        return ModResp.byErr(MSG_BAD_DATA);
    }

    @Override
    public @NonNull UploadResp<Integer> uploadEssay(Integer user, UploadEssay data) {
        if (user == null)
            return UploadResp.byErr(MSG_NOT_LOGIN);
        if (!withCache(() -> cache_category.containsKey(data.catalogue())))
            return UploadResp.byErr(MSG_NOT_FOUND_CATALOGUE);
        if (!withCache(() -> cache_category.get(data.catalogue()).containsKey(data.category())))
            return UploadResp.byErr(MSG_NOT_FOUND_CATEGORY);
        for (val tag : data.tags())
            if (tag.contains(","))
                return UploadResp.byErr(MSG_BAD_DATA);

        var essay = new Essay();
        essay.setCatalogueKey(data.catalogue());
        essay.setCategoryKey(data.category());
        essay.setSenderID(user);
        essay.setTitle(data.title());
        essay.setImg(data.imgs());
        essay.setContent(data.content());
        essay.setType(data.type());
        essay.setDescription(data.description());
        essay.setTags(String.join(",", data.tags()));
        try {
            essay = essayDao.save(essay);
            return UploadResp.byId(essay.getId());
        } catch (DataAccessException e) {
            return UploadResp.byErr(MSG_BAD_DATA);
        }
    }
}
