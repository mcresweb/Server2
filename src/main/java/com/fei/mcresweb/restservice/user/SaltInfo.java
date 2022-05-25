package com.fei.mcresweb.restservice.user;

import lombok.NonNull;
import lombok.Value;

/**
 * 服务器登录数据信息
 *
 * @author yuanlu
 */
@Value
public class SaltInfo {
    /**
     * 服务器的公共盐
     */
    @NonNull String salt;
    /**
     * 服务器当前时间
     */
    @NonNull String time;

    /**
     * 验证码VID
     */
    @NonNull String vaptcha;

    public SaltInfo(@NonNull String salt, long time, String vaptcha) {
        this.salt = salt;
        this.time = Long.toString(time);
        this.vaptcha = vaptcha == null ? "" : vaptcha;
    }
}
