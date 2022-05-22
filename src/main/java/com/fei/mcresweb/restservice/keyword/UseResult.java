package com.fei.mcresweb.restservice.keyword;

import lombok.NonNull;

/**
 * 使用结果
 *
 * @param success 是否成功
 * @param err     错误消息
 * @param expire  过期时间
 */
public record UseResult(boolean success, String err, Long expire) {
    public UseResult(@NonNull String err) {
        this(false, err, null);
    }

    public UseResult(long expire) {
        this(true, null, expire);
    }
}
