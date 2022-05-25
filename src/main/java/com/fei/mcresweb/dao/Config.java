package com.fei.mcresweb.dao;

import com.fei.mcresweb.defs.ConfType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 后端基础配置
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@NoArgsConstructor
@Table(name = "config")
public class Config {
    @Id
    @NonNull
    @Column(name = "type", nullable = false, updatable = false)
    private String type;
    @Setter
    @Getter
    @Column(name = "value", nullable = false, columnDefinition = "BLOB")
    private byte @NonNull [] value;

    @Getter
    @NonNull
    @Column(name = "clazz", nullable = false, updatable = false)
    private String clazz = "";

    public Config(@NonNull ConfType<?> ct) {
        type = ct.getName();
        clazz = ct.getDataType().getName();
    }

    public ConfType<?> getType() {
        return ConfType.ALL_TYPES_VIEW.get(type);
    }

}