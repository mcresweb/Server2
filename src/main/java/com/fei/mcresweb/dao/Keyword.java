package com.fei.mcresweb.dao;

import com.fei.mcresweb.defs.TokenLen;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

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
    @Column(nullable = false, updatable = false, columnDefinition = "CHAR(" + TokenLen.TOKEN_LEN + ")")
    String id;
    /**
     * 是否已经使用
     */
    @Comment("是否已经使用")
    boolean used = false;
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @NonNull
    @Column(nullable = false, updatable = false)
    @Comment("创建时间")
    Date generateTime;
    /**
     * 使用时间
     */
    @Comment("使用时间")
    @Column(insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date useTime;
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
    @Column(nullable = false, name = "generator", updatable = false)
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
    @Column(name = "user", insertable = false)
    Integer userID;
    /**
     * 价值
     */
    @Column(nullable = false, updatable = false)
    @Comment("价值(会员天数)")
    int value;
    /**
     * 过期时间,不指定则为永久
     */
    @Column(updatable = false)
    @Comment("过期时间,不指定则为永久")
    @Temporal(TemporalType.TIMESTAMP)
    Date expire;

    public Keyword(@NonNull String id, int generateUser, int value, Date expire) {
        setId(id);
        setUsed(false);
        setGenerateUserID(generateUser);
        setValue(value);
        setExpire(expire);
    }

    /**
     * 会员码
     */
    public void setId(@NonNull final String id) {
        if (id.length() != TokenLen.TOKEN_LEN)
            throw new IllegalArgumentException("Bad Token length: " + id.length());
        this.id = id;
    }
}
