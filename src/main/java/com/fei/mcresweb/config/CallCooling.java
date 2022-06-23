package com.fei.mcresweb.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 调用冷却
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallCooling {

    /**
     * 冷却毫秒数
     */
    long value();

    /**
     * 无视冷却的等级
     */
    UnlimitedType unlimited() default UnlimitedType.NO;

    /**
     * 使用IP标识用户<br>
     * 在登录用户上默认使用用户ID来标识用户, 使用此项强制使用IP来标识用户<br>
     * 配合 {@link UserAuth} 来限制匿名用户
     */
    boolean useIp() default false;

    enum UnlimitedType {
        /**
         * 登录用户可以无冷却
         */
        LOGIN,
        /**
         * VIP/ADMIN可以无冷却
         */
        VIP,
        /**
         * ADMIN可以无冷却
         */
        ADMIN,
        /**
         * 没有任何请求可以无冷却
         */
        NO;
        public static final String msgPath = "api-cooling";
    }
}
