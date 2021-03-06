package com.fei.mcresweb.dao;

import com.fei.mcresweb.restservice.content.ImgUsing;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
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
@FieldNameConstants
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
    //    @JoinColumn(nullable = false, name = "category", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "category"))
    @JoinColumns({
        @JoinColumn(nullable = false, name = "category", referencedColumnName = "`key`", insertable = false, updatable = false),
        @JoinColumn(nullable = false, name = "catalogue", referencedColumnName = "catalogue", insertable = false, updatable = false)})
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
    @Nullable
    String tags;
    /**
     * 内容文章的类型<br>
     * 不在后端进行任何解析, 由前端解析
     */
    @Comment("文章类型")
    @Nullable
    String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "essay", targetEntity = EssayFileInfo.class)
    List<EssayFileInfo> files;

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

    public void setTagsList(@Nullable Collection<String> tags) {
        setTags(tags == null ? null : String.join(tagDelimiter, tags));
    }

    @Nullable
    public List<String> getTagsList() {
        val tags = getTags();
        return tags == null ? null : Arrays.asList(tags.split(tagDelimiter));
    }

    /**
     * tag分隔符
     */
    public static final String tagDelimiter = ",";

    /**
     * @return 任意一个列表图片uuid
     */
    @Nullable
    public UUID getAnyListImg() {
        val arr = img.stream()//
            .filter(EssayImgs::isShowInList)//
            .map(EssayImgs::getImgId)//
            .toArray(UUID[]::new);
        if (arr.length > 0)
            return arr[RANDOM.nextInt(arr.length)];
        return null;
    }

    private static final Random RANDOM = new Random();

    public Map<UUID, ImgUsing> getImgUsing() {
        LinkedHashMap<UUID, ImgUsing> map = new LinkedHashMap<>();
        for (val img : this.img) {
            map.put(img.getImgId(), new ImgUsing(img));
        }
        return map;
    }

    public void setImg(@NonNull Map<UUID, ImgUsing> imgs) {
        this.img = imgs.entrySet().stream().map(e -> new EssayImgs(getId(), e.getKey(), e.getValue()))
            .collect(Collectors.toSet());
    }
}
