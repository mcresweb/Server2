package com.fei.mcresweb.restservice.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 我的用户信息
 *
 * @author yuanlu
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyUserInfo {
    /**
     * 是否处于登录状态
     */
    boolean login;
    /**
     * 用户UUID
     */
    Integer id;
    /**
     * 用户名
     */
    String name;
    /**
     * 电子邮箱
     */
    String email;
    /**
     * 是否是管理员
     */
    Boolean admin;
    /**
     * 是否是VIP
     */
    Boolean vip;
    /**
     * 是否被锁定
     */
    Boolean lock;

    /**
     * 通过已登录的数据构造一个信息
     */
    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull MyUserInfo byLogin(@NonNull Integer id, @NonNull String name, @NonNull String email,
        boolean admin, boolean vip, boolean lock) {
        return new MyUserInfo(true, id, name, email, admin, vip, lock);
    }
}
