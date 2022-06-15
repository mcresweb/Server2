package com.fei.mcresweb.restservice.user;

import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.defs.VipLevel;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * VIP信息
 *
 * @param vipName vip名称
 * @param vipTag  vip内部标记
 * @param expire  过期时间戳
 */
public record VipInfo(@NonNull String vipName, @NonNull String vipTag, @Nullable Long expire) {
    public VipInfo(@NotNull VipLevel vip, Long expire) {
        this(vip.getName(), vip.name().toLowerCase(Locale.ENGLISH), expire);
    }

    public VipInfo(@NotNull User user) {
        this(user.isVip() ? user.getVipLvl() : VipLevel.NONE, user.isVip() ? user.getVipExpire().getTime() : null);
    }
}
