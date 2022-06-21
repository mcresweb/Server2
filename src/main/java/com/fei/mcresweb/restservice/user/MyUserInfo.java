package com.fei.mcresweb.restservice.user;

import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.defs.VipLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 我的用户信息
 *
 * @author yuanlu
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyUserInfo {
    /**
     * 未登录
     */
    public static final MyUserInfo NOT_LOGIN = new MyUserInfo(false, null, null, null, null, null, null, null);
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
     * VIP名称
     */
    String vipName;
    /**
     * 是否被锁定
     */
    Boolean lock;

    /**
     * 通过已登录的数据构造一个信息
     */
    @Contract("_, _, _, _, _, _, _ -> new")
    public static @NotNull MyUserInfo byLogin(@NonNull Integer id, @NonNull String name, @NonNull String email,
        boolean admin, boolean isVip, @Nullable VipLevel vip, boolean lock) {
        return new MyUserInfo(true, id, name, email, admin, isVip && vip != null && vip.isVip(),
            (vip == null ? VipLevel.NONE : vip).getName(), lock);
    }

    /**
     * 从数据库构建
     *
     * @param user 数据库数据
     * @return 他人信息
     */
    @Contract("_ -> new")
    public static @NotNull MyUserInfo fromDatabase(@NonNull User user) {
        return byLogin(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin(), user.isVip(),
            user.getVipLvl(), false/*TODO*/);
    }
}
