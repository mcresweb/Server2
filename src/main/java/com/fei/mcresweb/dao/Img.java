package com.fei.mcresweb.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Objects;
import java.util.UUID;

/**
 * 图片数据
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "img")
public class Img {
    /**
     * 图片ID
     */
    @NonNull
    @Id
    @Column(columnDefinition = "BINARY(16)")
    @Comment("图片UUID")
    UUID id;

    /**
     * 原始图片
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    @Comment("原始图片")
    @ToString.Exclude
    @Lob
    Blob img;

    /**
     * 略缩图片
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "BLOB")
    @Comment("略缩图片")
    @ToString.Exclude
    @Lob
    Blob thu;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Img img = (Img)o;
        return id != null && Objects.equals(id, img.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
