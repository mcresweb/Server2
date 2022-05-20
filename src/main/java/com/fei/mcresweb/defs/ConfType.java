package com.fei.mcresweb.defs;

import com.fei.mcresweb.dao.Config;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * 配置文件类型
 */
@Getter
@ToString
public abstract class ConfType<T> {
    /**
     * 此配置项的名称
     */
    @ToString.Include
    private final String name;
    /**
     * 此配置项的数据类型
     */
    private final Class<T> dataType;

    @Getter
    protected T cache;

    /**
     * 所有类型
     */
    private static final Map<String, ConfType<?>> ALL_TYPES = new LinkedHashMap<>();
    /**
     * 所有类型
     */
    public static final Map<String, ConfType<?>> ALL_TYPES_VIEW = Collections.unmodifiableMap(ALL_TYPES);

    /**
     * 任意数据的前缀
     */
    public static final ConfType<String> STR_PREFIX = new ConfType<>("string_prefix", String.class) {
        @Override
        public String getData(@NonNull Config conf) {
            return cache = new String(conf.getValue(), StandardCharsets.UTF_8);
        }

        @Override
        public void setByte(@NonNull Config conf, @NonNull String data) {
            conf.setValue(data.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public @NonNull String summon() {
            return String.format("MRW-%s-", Long.toUnsignedString(new Random().nextLong(), Character.MAX_RADIX));
        }
    };

    /**
     * 登录有效期
     */
    public static final ConfType<Long> LOGIN_EXP = new ConfType<Long>("jwt_expired", Long.class) {
        /**有效期(ms)*/
        @SuppressWarnings("FieldCanBeLocal")
        private final long exp = 1000 * 60 * 60 * 24 * 7 - 1;

        @Override
        public Long getData(@NonNull Config conf) {
            return cache = readLong(conf.getValue());
        }

        @Override
        public void setByte(@NonNull Config conf, @NonNull Long data) {
            conf.setValue(writeLong(cache = data));
        }

        @Override
        public @NonNull Long summon() {
            return exp;
        }

        public long readLong(byte[] readBuffer) {
            return (((long)readBuffer[0] << 56) + ((long)(readBuffer[1] & 255) << 48) + ((long)(readBuffer[2] & 255)
                << 40) + ((long)(readBuffer[3] & 255) << 32) + ((long)(readBuffer[4] & 255) << 24) + (
                (readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8) + ((readBuffer[7] & 255)));
        }

        public byte[] writeLong(long v) {
            byte[] writeBuffer = new byte[8];
            writeBuffer[0] = (byte)(v >>> 56);
            writeBuffer[1] = (byte)(v >>> 48);
            writeBuffer[2] = (byte)(v >>> 40);
            writeBuffer[3] = (byte)(v >>> 32);
            writeBuffer[4] = (byte)(v >>> 24);
            writeBuffer[5] = (byte)(v >>> 16);
            writeBuffer[6] = (byte)(v >>> 8);
            writeBuffer[7] = (byte)(v);
            return writeBuffer;
        }
    };

    /**
     * 登录时间戳范围
     */
    public static final ConfType<Long> LOGIN_TIME_RANGE = new ConfType<Long>("login_time_range", Long.class) {
        /**有效期(ms)*/
        @SuppressWarnings("FieldCanBeLocal")
        private final long exp = 1000 * 60;

        @Override
        public Long getData(@NonNull Config conf) {
            return cache = readLong(conf.getValue());
        }

        @Override
        public void setByte(@NonNull Config conf, @NonNull Long data) {
            conf.setValue(writeLong(cache = data));
        }

        @Override
        public @NonNull Long summon() {
            return exp;
        }

        @Contract(pure = true)
        public long readLong(byte @NotNull [] readBuffer) {
            return (((long)readBuffer[0] << 56) + ((long)(readBuffer[1] & 255) << 48) + ((long)(readBuffer[2] & 255)
                << 40) + ((long)(readBuffer[3] & 255) << 32) + ((long)(readBuffer[4] & 255) << 24) + (
                (readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8) + ((readBuffer[7] & 255)));
        }

        @Contract(pure = true)
        public byte @NotNull [] writeLong(long v) {
            byte[] writeBuffer = new byte[8];
            writeBuffer[0] = (byte)(v >>> 56);
            writeBuffer[1] = (byte)(v >>> 48);
            writeBuffer[2] = (byte)(v >>> 40);
            writeBuffer[3] = (byte)(v >>> 32);
            writeBuffer[4] = (byte)(v >>> 24);
            writeBuffer[5] = (byte)(v >>> 16);
            writeBuffer[6] = (byte)(v >>> 8);
            writeBuffer[7] = (byte)(v);
            return writeBuffer;
        }
    };

    /**
     * JWT私钥
     */
    public static final ConfType<byte[]> JWT_KEY = new ConfType<>("jwt_private_key", byte[].class) {

        /**密钥长度*/
        @SuppressWarnings("FieldCanBeLocal")
        private final int len = 1024;

        @Override
        public byte[] getData(@NonNull Config conf) {
            return cache = conf.getValue();
        }

        @Override
        public synchronized void setByte(@NonNull Config conf, byte @NotNull [] data) {
            conf.setValue(cache = data);
        }

        @Override
        public byte @NonNull [] summon() {
            byte[] data = new byte[len];
            new SecureRandom().nextBytes(data);
            return data;
        }

    };

    /**
     * 公共salt
     */
    public static final ConfType<String> LOGIN_SALT = new ConfType<>("public_salt", String.class) {
        @Override
        public String getData(@NonNull Config conf) {
            return cache = new String(conf.getValue(), StandardCharsets.UTF_8);
        }

        @Override
        public void setByte(@NonNull Config conf, @NonNull String data) {
            conf.setValue(data.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public @NonNull String summon() {
            return Long.toUnsignedString(new Random().nextLong(), Character.MAX_RADIX);
        }
    };

    /**
     * 获取数据
     *
     * @param conf 数据库Entity
     * @return 数据
     */
    public abstract T getData(@NonNull Config conf);

    /**
     * 设置数据
     *
     * @param conf 数据库Entity
     * @param data 数据
     */
    public abstract void setByte(@NonNull Config conf, @NonNull T data);

    /**
     * 生成数据
     */
    public abstract @NonNull T summon();

    private ConfType(String name, Class<T> dataType) {
        this.name = name;
        this.dataType = dataType;
        synchronized (ALL_TYPES) {
            ALL_TYPES.put(name, this);
        }
    }
}
