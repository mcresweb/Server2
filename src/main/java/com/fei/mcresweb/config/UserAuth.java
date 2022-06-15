package com.fei.mcresweb.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户权限验证
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserAuth {

    AuthType value();

    enum AuthType {
        LOGIN, VIP, ADMIN;
    }
}
