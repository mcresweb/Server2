package com.fei.mcresweb.defs;

import com.fei.mcresweb.dao.Config;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置文件类型
 */
@Getter
@ToString
public abstract class ConfType<T> {
    /**
     * 所有类型
     */
    private static final Map<String, ConfType<?>> ALL_TYPES = new LinkedHashMap<>();
    /**
     * 所有类型
     */
    public static final Map<String, ConfType<?>> ALL_TYPES_VIEW = Collections.unmodifiableMap(ALL_TYPES);

    static abstract class ConfTypeString extends ConfType<String> {

        ConfTypeString(@NonNull String name) {
            super(name, String.class);
        }

        @Override
        public String getData(@NonNull Config conf) {
            return cache = new String(conf.getValue(), StandardCharsets.UTF_8);
        }

        @Override
        public void setByte(@NonNull Config conf, @NonNull String data) {
            conf.setValue(data.getBytes(StandardCharsets.UTF_8));
        }

    }

    static abstract class ConfTypeLong extends ConfType<Long> {

        ConfTypeLong(@NonNull String name) {
            super(name, Long.class);
        }

        @Override
        public Long getData(@NonNull Config conf) {
            return cache = readLong(conf.getValue());
        }

        @Override
        public void setByte(@NonNull Config conf, @NonNull Long data) {
            conf.setValue(writeLong(cache = data));
        }

        @Contract(pure = true)
        protected long readLong(byte @NotNull [] readBuffer) {
            return (((long)readBuffer[0] << 56) + ((long)(readBuffer[1] & 255) << 48) + ((long)(readBuffer[2] & 255)
                << 40) + ((long)(readBuffer[3] & 255) << 32) + ((long)(readBuffer[4] & 255) << 24) + (
                (readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8) + ((readBuffer[7] & 255)));
        }

        @Contract(pure = true)
        protected byte @NotNull [] writeLong(long v) {
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
    }

    static abstract class ConfTypeInt extends ConfType<Integer> {

        ConfTypeInt(@NonNull String name) {
            super(name, Integer.class);
        }

        @Override
        public Integer getData(@NonNull Config conf) {
            return cache = readInt(conf.getValue());
        }

        @Override
        public void setByte(@NonNull Config conf, @NonNull Integer data) {
            conf.setValue(writeInt(cache = data));
        }

        @Contract(pure = true)
        protected int readInt(byte @NotNull [] readBuffer) {
            return ((readBuffer[0] << 24) + (readBuffer[1] << 16) + (readBuffer[2] << 8) + (readBuffer[3]));
        }

        @Contract(pure = true)
        protected byte @NotNull [] writeInt(int v) {
            byte[] writeBuffer = new byte[8];
            writeBuffer[0] = (byte)(v >>> 24);
            writeBuffer[1] = (byte)(v >>> 16);
            writeBuffer[2] = (byte)(v >>> 8);
            writeBuffer[3] = (byte)(v);
            return writeBuffer;
        }
    }

    static abstract class ConfTypeByteArr extends ConfType<byte[]> {

        ConfTypeByteArr(@NonNull String name) {
            super(name, byte[].class);
        }

        @Override
        public byte[] getData(@NonNull Config conf) {
            return cache = conf.getValue();
        }

        @Override
        public void setByte(@NonNull Config conf, byte @NonNull [] data) {
            conf.setValue(cache = data);
        }
    }

    /**
     * 此配置项的名称
     */
    @ToString.Include
    @NonNull
    private final String name;
    /**
     * 此配置项的数据类型
     */
    @NonNull
    private final Class<T> dataType;
    @Getter
    protected T cache;

    private ConfType(@NonNull String name, @NonNull Class<T> dataType) {
        this.name = name;
        this.dataType = dataType;
        synchronized (ALL_TYPES) {
            ALL_TYPES.put(name, this);
        }
    }

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
}
