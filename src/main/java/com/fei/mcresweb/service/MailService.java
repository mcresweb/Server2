package com.fei.mcresweb.service;

import lombok.NonNull;

import java.util.Locale;

/**
 * 邮件接口
 */
public interface MailService {

    /**
     * 发送一封邮件
     *
     * @param to      接收者
     * @param subject 主题
     * @param content html内容
     * @return 是否发送成功
     */
    boolean send(@NonNull String to, @NonNull String subject, @NonNull String content);

    /**
     * 发送一封注册时验证邮箱的邮件
     *
     * @param to     接收者
     * @param locale loc
     * @return 是否发送成功
     */
    boolean sendRegisterCode(@NonNull String to, @NonNull Locale locale);

    /**
     * 检查注册验证码是否正确
     *
     * @param to   邮箱
     * @param code 邮箱验证码
     * @return 是否验证通过
     */
    boolean checkRegisterCode(@NonNull String to, @NonNull String code);
}
