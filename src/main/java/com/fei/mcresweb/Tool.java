package com.fei.mcresweb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.jetbrains.annotations.Contract;

import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tool {
    public static String byte2hex(byte[] buf) {
        return new BigInteger(1, buf).toString(16);
    }

    /**
     * 验证字符串是否有效
     *
     * @param str 字符串
     * @return 非null且非空
     */
    @Contract("null->false")
    public static boolean valid(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 获取数据表名称
     *
     * @param c 数据表entity
     * @return 数据表名称
     */
    public static String tableName(@NonNull Class<?> c) {
        return tableNameCache.computeIfAbsent(c, k -> {
            val table = c.getDeclaredAnnotation(Table.class);
            if (table == null || table.name() == null || table.name().isEmpty())
                return ImprovedNamingStrategy.INSTANCE.classToTableName(c.getName());
            return ImprovedNamingStrategy.INSTANCE.tableName(table.name());
        });
    }

    private static final Map<Class<?>, String> tableNameCache = new ConcurrentHashMap<>();

}
