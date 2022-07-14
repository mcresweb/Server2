package com.fei.mcresweb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
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
@Log
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
     *
     * @param random 随机器
     * @param length 数字长度
     * @return 数字字符串
     */
    @Contract("_, _ -> new")
    public static @NotNull String randomNumber(Random random, int length) {
        char[] ch = new char[length];
        while (length-- > 0)
            ch[length] = (char)(random.nextInt(10) + '0');
        return new String(ch);
    }

    /**
     * 解析字符串含义<br>
     * 使用指定字符包围的字符串将作为变量名解析为对应数据<br>
     * 例子:
     *
     * <pre>
     * msg: 你好{@code <player>}, 我是{@code <who>}, 现在已经{@code <time>}点了！
     * start: {@code '<'}
     * end: {@code '>'}
     * vars:{
     *     player: yuanlu,
     *     who: Administrators
     *     time: 7
     * }
     *
     * return: 你好yuanlu, 我是Administrators, 现在已经7点了！
     * </pre>
     *
     * @param msg   原消息
     * @param start 变量开始字符
     * @param end   变量结束字符
     * @param vars  变量集合
     * @return 解析后字符串
     */
    @Contract(value = "null,_,_,_->null;!null,_,_,_->!null", pure = true)
    public static String parseVar(final String msg, char start, char end, @NonNull Map<String, Object> vars) {
        int startIndex;
        if (msg == null || (startIndex = msg.indexOf(start)) < 0)
            return msg;
        try {
            StringBuilder sb = new StringBuilder();
            if (startIndex > 0)
                sb.append(msg, 0, startIndex);
            for (int i = startIndex; i < msg.length(); i++) {
                char c = msg.charAt(i);

                if (c == start) {// 找到开始标记
                    int e = msg.indexOf(end, i + 1);// 寻找结束标记
                    if (e < 0) {// 没有结束标记
                        sb.append(msg.substring(i));
                        return sb.toString();// 直接返回
                    }

                    String var_str = msg.substring(i + 1, e);// 变量名
                    if (var_str.isEmpty()) {// 变量名为空
                        if (start == end)
                            sb.append(start);// 转意
                        else
                            sb.append(start).append(end);// 不是变量
                    } else {

                        if (vars.containsKey(var_str)) {// 是变量
                            Object var = vars.get(var_str);
                            sb.append(var);
                        } else {// 不是变量
                            sb.append(start).append(var_str).append(end);
                        }

                    }
                    i = e;// 跳过变量部分
                } else {
                    sb.append(c);// 普通字符
                }
            }
            return sb.toString();
        } catch (Throwable e) {
            log.warning("不能翻译字符串: " + msg);
            e.printStackTrace();
            return msg;
        }
    }
}
