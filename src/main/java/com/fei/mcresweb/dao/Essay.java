package com.fei.mcresweb.dao;

import com.fei.mcresweb.service.ContentService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Comment;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 内容数据
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "essay")
public class Essay {

    /**
     * 内容ID
     */
    @Id
    @Comment("内容ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    /**
     * 所属大分类
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "catalogue", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("大分类")
    Catalogue catalogue;

    /**
     * 所属大分类的key
     */
    @NonNull
    @Column(nullable = false, name = "catalogue")
    String catalogueKey;

    /**
     * 所属小分类
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("小分类")
    Category category;

    /**
     * 所属小分类的key
     */
    @NonNull
    @Column(nullable = false, name = "category")
    String categoryKey;

    /**
     * 发布者
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "sender", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("发布者")
    User sender;

    /**
     * 发布者ID
     */
    @Column(nullable = false, name = "sender")
    int senderID;

    /**
     * 内容标题
     */
    @NonNull
    @Column(nullable = false)
    @Comment("内容标题")
    String title;

    /**
     * 评分
     */
    @Comment("评分")
    Double star;

    /**
     * 评分人数
     */
    @Column(nullable = false)
    @Comment("评分人数")
    long starAmount = 0;

    /**
     * 下载数
     */
    @Column(nullable = false)
    @Comment("下载次数")
    long download = 0;

    /**
     * 图片
     */
    @NonNull
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "essay")
    @ToString.Exclude
    @Comment("描述图片")
    Set<EssayImgs> img;

    /**
     * 内容文章
     */
    @NonNull
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    @Comment("内容文章")
    String content;

    /**
     * 内容文章的类型<br>
     * 不在后端进行任何解析, 由前端解析
     */
    @NonNull
    @Column(nullable = false)
    @Comment("文章类型")
    String type;

    /**
     * 内容标签
     */
    @Comment("内容标签, 使用英文逗号分割")
    String tags;
    /**
     * 内容文章的类型<br>
     * 不在后端进行任何解析, 由前端解析
     */
    @Comment("文章类型")
    String description;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Essay essay = (Essay)o;
        return Objects.equals(id, essay.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return 任意一个图片uuid
     */
    @Nullable
    public UUID getAnyImg() {
        val itr = img.iterator();
        if (itr.hasNext())
            return itr.next().getImgId();
        return null;
    }

    /**
     * @return 所有图片UUID
     */
    public List<UUID> getImgUUID() {
        return img.stream().map(EssayImgs::getImgId).toList();
    }

    public void setImg(@NonNull Map<UUID, ContentService.ImgUsing> imgs) {
        this.img = imgs.entrySet().stream().map(e -> new EssayImgs(getId(), e.getKey(), e.getValue()))
            .collect(Collectors.toSet());
    }
}
