package com.fei.mcresweb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
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

    /**
     * 随机数字
     * @param random 随机器
     * @param length 数字长度
     * @return 数字字符串
     */
    @Contract("_, _ -> new")
    public static @NotNull String randomNumber(Random random, int length) {
        char[] ch=new char[length];
        while(length-->0)ch[length]= (char) (random.nextInt(10)+'0');
        return new String(ch);
    }
}
