package com.fei.mcresweb.service;

import com.fei.mcresweb.restservice.user.*;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

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
    LoginInfo login(@NotNull Locale locale, @NonNull loginReq req);

    /**
     * 注册
     *
     * @param req 请求
     * @return 注册信息
     */
    RegisterInfo register(@NotNull Locale locale, @NonNull registerReq req);

    /**
     * 判断输入是否可能是用户ID
     *
     * @param str 输入字符串
     * @return 是否肯能是user id
     */
    boolean maybeUserIdString(@NotNull String str);

    /**
     * 判断输入是否可能是用户邮箱
     *
     * @param str 输入字符串
     * @return 是否肯能是user email
     */
    boolean maybeUserEmailString(@NotNull String str);

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
    @NonNull MyUserInfo infoMe(Integer id);

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
     * 判断用户是否是管理员
     *
     * @param user 用户
     * @return 是否是管理员
     */
    @Contract("null->false")
    boolean isAdmin(Integer user);

    /**
     * 设置验证码数据
     *
     * @param data 数据
     */
    void setVaptcha(SetVaptchaReq data);

    /**
     * 判断用户是否是VIP / 管理员
     *
     * @param user 用户
     * @return 是否是VIP
     */
    @Contract("null->false")
    default boolean isVip(Integer user) {
        return isVip(user, false);
    }

    /**
     * 判断用户是否是VIP
     *
     * @param user 用户
     * @param real 是否真实判断(为false时用户是管理员也被当做VIP)
     * @return 是否是VIP
     */
    @Contract("null,_->false")
    boolean isVip(Integer user, boolean real);

    /**
     * 列出用户的VIP信息
     *
     * @param user 用户
     * @return VIP信息
     */
    @Contract("null->null")
    @Nullable
    VipInfo vipUser(Integer user);

    /**
     * @return 用户数量
     */
    long countUser();

    /**
     * 判断用户是否存在
     *
     * @param id 用户ID
     * @return 用户是否存在
     */
    boolean isUser(Integer id);

    /**
     * 判断服务器是否需要初始化, 即: 没有任何一个管理员用户
     *
     * @return 是否需要初始化
     */
    boolean needInit();

    /**
     * 设置admin
     *
     * @param id 用户ID
     */
    void setAdmin(int id);

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

    /**
     * 设置验证码请求
     *
     * @param vid vid
     * @param key key
     */
    record SetVaptchaReq(String vid, String key) {
    }

    ;
}
