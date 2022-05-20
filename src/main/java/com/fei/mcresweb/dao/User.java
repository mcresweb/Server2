package com.fei.mcresweb.dao;

import com.fei.mcresweb.defs.VipLevel;
import com.fei.mcresweb.restservice.user.LoginInfo;
import com.fei.mcresweb.restservice.user.RegisterInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 用户信息
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "user")
public class User {
    /**
     * 用户标识符
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("用户ID")
    int id;
    /**
     * 用户名
     */
    @Column(nullable = false, unique = true)
    @NonNull
    @Comment("用户名")
    String username;
    /**
     * 密码
     */
    @Column(nullable = false)
    @NonNull
    @Comment(" mima")
    String password;
    /**
     * 邮箱
     */
    @Column(nullable = false)
    @NonNull
    @Comment("邮箱")
    String email;
    /**
     * vip等级
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    @NonNull
    @Comment("VIP等级")
    VipLevel vipLvl = VipLevel.NONE;
    /**
     * vip过期时间
     */
    @Comment("VIP到期时间")
    ZonedDateTime vipExpire;

    /**
     * 是否是管理员
     */
    @Comment("是否是管理员")
    boolean isAdmin = false;
    /**
     * 注册时间
     */
    @CreationTimestamp
    @Column(nullable = false)
    @NonNull
    @Comment("账户创建时间")
    ZonedDateTime createTime;

    public LoginInfo toLoginInfo() {
        return LoginInfo.byUserId(id);
    }

    public RegisterInfo toRegisterInfo() {
        return RegisterInfo.byUserId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        User user = (User)o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
