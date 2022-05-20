package com.fei.mcresweb.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * 大分类信息
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "category")
public class Category {
    /**
     * 大分类标识符
     */
    @Id
    @NonNull
    @Column(nullable = false, name = "`key`")
    @Comment("小分类标识符")
    String key;

    /**
     * 所属大分类
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "catalogue", insertable = false, updatable = false)
    @NonNull
    @ToString.Exclude
    @Comment("所属大分类")
    Catalogue catalogue;

    /**
     * 所属大分类的key
     */
    @NonNull
    @Column(nullable = false, name = "catalogue")
    String catalogueKey;

    /**
     * 序号
     */
    @Column(nullable = false, name = "`index`")
    @Comment("排序序号")
    double index = .0;
    /**
     * 此大类的图片
     */
    @Column(nullable = false, unique = true)
    @NonNull
    @Comment("分类标题")
    String title;

    /**
     * 图片
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "img", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("描述图片")
    Img img;
    /**
     * 图片UUID
     */
    @Column(name = "img", columnDefinition = "BINARY(16)")
    UUID imgID;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Category category = (Category)o;
        return key != null && Objects.equals(key, category.key);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
