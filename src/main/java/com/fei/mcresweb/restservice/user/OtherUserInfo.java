package com.fei.mcresweb.restservice.user;

import lombok.NonNull;
import lombok.Value;

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
    @NonNull int id;
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
}
