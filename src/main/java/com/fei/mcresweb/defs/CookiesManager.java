package com.fei.mcresweb.defs;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Arrays;

/**
 * 所有Cookie的定义
 */
@Getter
@Component
public class CookiesManager {
    final String[] keys;

    public CookiesManager(ConfigManager configManager) {
        var prefix = configManager.getOrSummon(ConfType.STR_PREFIX, true);
        keys = Arrays.stream(CookiesField.values())//
            .map(CookiesField::name)//
            .map(String::toLowerCase)//
            .map(prefix::concat)//
            .toArray(String[]::new);
    }

    /**
     * 获取cookie的键
     *
     * @param field cookie类型
     * @return 键
     */
    public @NonNull String key(@NonNull CookiesField field) {
        return keys[field.ordinal()];
    }

    /**
     * 创建cookie
     *
     * @param field cookie类型
     * @param value cookie值
     * @return cookie
     */
    @Contract("_,_->new")
    public Cookie cookie(@NonNull CookiesField field, String value) {
        var cook = new Cookie(keys[field.ordinal()], value);
        cook.setPath("/");
        return cook;
    }

    public enum CookiesField {
        LOGIN;
        private String cache;

    }
}