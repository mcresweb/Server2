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
    long time;
}
