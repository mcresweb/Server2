package com.fei.mcresweb.service;

import com.fei.mcresweb.restservice.user.LoginInfo;
import com.fei.mcresweb.restservice.user.RegisterInfo;
import lombok.NonNull;

/**
 * 用户服务
 *
 * @author yuanlu
 */
public interface UserService {
    /**
     * 登录
     *
     * @param req 请求
     * @return 登录信息
     */
    LoginInfo login(@NonNull loginReq req);

    /**
     * 注册
     *
     * @param req 请求
     * @return 注册信息
     */
    RegisterInfo register(@NonNull registerReq req);

    /**
     * 登录请求
     *
     * @param username 用户名
     * @param password 二次摘要
     * @param time     登录时间戳
     */
    record loginReq(@NonNull String username, @NonNull String password, long time) {
    }

    /**
     * 注册请求
     *
     * @param username 用户名
     * @param password 一次摘要
     * @param email    邮箱
     * @param code     邮箱验证码
     */
    record registerReq(@NonNull String username, @NonNull String password, @NonNull String email,
                       @NonNull String code) {
    }
}
