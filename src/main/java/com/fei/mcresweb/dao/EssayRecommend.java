package com.fei.mcresweb.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 内容推荐数据
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "essay_recommend")
public class EssayRecommend {
    /**
     * 内容
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "essay_id", insertable = false, updatable = false)
    @NonNull
    @ToString.Exclude
    Essay essay;
    /**
     * 内容ID
     */
    @Id
    @Column(nullable = false, updatable = false, name = "essay_id")
    int essayId;

    /**
     * 推荐过期时间
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("推荐到期时间")
    Date expire;

    /**
     * 推荐过期时间
     */
    @Column(nullable = false)
    @Comment("提升时间")
    long hoist;

    /**
     * 推荐者
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "sender", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("推荐者")
    User sender;
    /**
     * 推荐者ID
     */
    @Column(nullable = false, name = "sender")
    int senderID;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        EssayRecommend that = (EssayRecommend)o;
        return Objects.equals(essayId, that.essayId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
