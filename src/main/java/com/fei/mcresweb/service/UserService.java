package com.fei.mcresweb.service;

import com.fei.mcresweb.restservice.user.LoginInfo;
import com.fei.mcresweb.restservice.user.MyUserInfo;
import com.fei.mcresweb.restservice.user.OtherUserInfo;
import com.fei.mcresweb.restservice.user.RegisterInfo;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.springframework.lang.Nullable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
     * 列出他人信息
     *
     * @param id 用户ID
     * @return 信息
     */
    @Nullable
    OtherUserInfo infoOther(int id);

    /**
     * 列出个人信息
     *
     * @param id 用户ID
     * @return 信息
     */
    @Nullable
    MyUserInfo infoMe(int id);

    /**
     * 生成登录令牌
     *
     * @param user 用户
     * @return 登录令牌
     */
    @NonNull String summonToken(int user);

    /**
     * 生成登录令牌Cookie
     *
     * @param user 用户
     * @return 登录令牌Cookie
     */
    @NonNull Cookie summonTokenCookie(int user);

    /**
     * 验证令牌
     *
     * @param token 登录令牌
     * @return 用户ID / null
     */
    @Nullable
    @Contract("null->null;_->_")
    Integer checkToken(String token);

    /**
     * 获取用户ID
     *
     * @param req 用户请求
     * @return 用户ID / 未登录
     */
    Integer getUserIdByCookie(@NonNull HttpServletRequest req);

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
