package com.fei.mcresweb.defs;

import com.fei.mcresweb.dao.Config;
import com.fei.mcresweb.dao.ConfigDao;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ConfigManager {
    private final ConfigDao configDao;

    public ConfigManager(ConfigDao configDao) {
        this.configDao = configDao;

    }

    /**
     * 获取配置文件
     *
     * @param ct 配置类型
     * @return 数据
     */
    public <T> @Nullable T getConfig(@NonNull ConfType<T> ct, boolean cache) {
        if (cache) {
            val obj = ct.getCache();
            if (obj != null)
                return obj;
        }
        val conf = configDao.findById(ct.getName());
        return conf.map(ct::getData).orElse(null);
    }

    /**
     * 设置配置文件
     *
     * @param ct   配置类型
     * @param data 数据
     */
    @Contract("_,_ -> param2")
    public <T> T setConfig(@NonNull ConfType<T> ct, T data) {
        val conf = new Config(ct);
        ct.setByte(conf, data);
        configDao.save(conf);
        return data;
    }

    /**
     * 获取或生成
     *
     * @param ct 配置类型
     * @return 数据
     */
    public <T> @NonNull T getOrSummon(@NonNull ConfType<T> ct, boolean cache) {
        T data = getConfig(ct, cache);
        if (data != null)
            return data;
        data = ct.summon();

        val conf = new Config(ct);
        ct.setByte(conf, data);
        configDao.save(conf);

        return data;
    }

}
