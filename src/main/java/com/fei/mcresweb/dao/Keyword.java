package com.fei.mcresweb.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * 会员码
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "keyword")
public class Keyword {
    /**
     * 会员码
     */
    @Id
    @NonNull
    @Comment("会员码")
    String id;

    /**
     * 是否已经使用
     */
    @Comment("是否已经使用")
    boolean used;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false)
    @NonNull
    @Comment("创建时间")
    ZonedDateTime generateTime;

    /**
     * 使用时间
     */
    @Comment("使用时间")
    ZonedDateTime useTime;

    /**
     * 生成者
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "generator", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("生成者")
    User generateUser;

    /**
     * 生成者ID
     */
    @Column(nullable = false, name = "generator")
    int generateUserID;
    /**
     * 生成者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", insertable = false, updatable = false)
    @ToString.Exclude
    @Comment("使用者")
    User user;

    /**
     * 生成者ID
     */
    @Column(name = "user")
    int userID;
}
