package com.fei.mcresweb.restservice.user;

import com.fei.mcresweb.dao.User;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 其它用户信息
 *
 * @author yuanlu
 */
@Value
public class OtherUserInfo {
    /**
     * 用户UUID
     */
    int id;
    /**
     * 用户名
     */
    @NonNull String name;
    /**
     * 是否是管理员
     */
    boolean admin;
    /**
     * 是否是VIP
     */
    boolean vip;
    /**
     * 是否被锁定
     */
    boolean lock;

    /**
     * 从数据库构建
     *
     * @param user 数据库数据
     * @return 他人信息
     */
    @Contract("_ -> new")
    public static @NotNull OtherUserInfo fromDatabase(@NonNull User user) {
        return new OtherUserInfo(user.getId(), user.getUsername(), user.isAdmin(), user.isVip(), false/*TODO*/);
    }

}
