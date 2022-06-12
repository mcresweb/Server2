package com.fei.mcresweb.dao;

import com.fei.mcresweb.service.ContentService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * 内容 - 图片
 */
@Entity
@IdClass(EssayImgsPK.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@NoArgsConstructor
@Table(name = "essay_imgs")
public class EssayImgs {

    /**
     * 内容
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "essay_id", insertable = false, updatable = false)
    @NonNull
    @ToString.Exclude
    Essay essay;
    /**
     * 内容ID
     */
    @Column(nullable = false, name = "essay_id")
    int essayId;

    /**
     * 是否要在头部显示
     */
    @Column(nullable = false)
    @Comment("是否在内容页头部展示")
    boolean showInHead = false;
    /**
     * 是否要在内容列表显示
     */
    @Column(nullable = false)
    @Comment("是否在内容列表展示")
    boolean showInList = false;

    /**
     * 图片
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "img_id", insertable = false, updatable = false)
    @NonNull
    @ToString.Exclude
    Img img;
    /**
     * 图片UUID
     */
    @NonNull
    @Column(nullable = false, name = "img_id", columnDefinition = "BINARY(16)")
    UUID imgId;

    public EssayImgs(int essayId, @NonNull UUID imgId, @NonNull ContentService.ImgUsing type) {
        setEssayId(essayId);
        setImgId(imgId);
        setShowInHead(type.head());
        setShowInList(type.list());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        EssayImgs essayImgs = (EssayImgs)o;
        return essay != null && Objects.equals(essay, essayImgs.essay) && img != null && Objects.equals(img,
            essayImgs.img);
    }

    @Override
    public int hashCode() {
        return Objects.hash(essay, img);
    }
}
