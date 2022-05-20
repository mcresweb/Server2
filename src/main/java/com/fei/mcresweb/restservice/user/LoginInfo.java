package com.fei.mcresweb.restservice.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 登录结果信息
 *
 * @author yuanlu
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginInfo {
    /**
     * 是否成功
     */
    boolean success;
    /**
     * 用户id 仅在success为true时存在
     */
    Integer userid;
    /**
     * 错误消息 仅在success为false时存在
     */
    String err;

    /**
     * 登录成功
     *
     * @param userid 用户ID
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull LoginInfo byUserId(int userid) {
        return new LoginInfo(true, userid, null);
    }

    /**
     * 登录失败
     *
     * @param err 错误消息
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull LoginInfo byErr(@NonNull String err) {
        return new LoginInfo(false, null, err);
    }

}
