package com.fei.mcresweb.defs;

import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.dao.Config;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class Configs {
    /**
     * 任意数据的前缀
     */
    public static final ConfType<String> STR_PREFIX = new ConfType.ConfTypeString("string_prefix") {

        @Override
        public @NonNull String summon() {
            return String.format("MRW-%s-", Long.toUnsignedString(new Random().nextLong(), Character.MAX_RADIX));
        }
    };

    /**
     * 登录有效期
     */
    public static final ConfType<Long> LOGIN_EXP = new ConfType.ConfTypeLong("jwt_expired") {
        /**有效期(ms)*/
        @SuppressWarnings("FieldCanBeLocal")
        private final long exp = 1000 * 60 * 60 * 24 * 7 - 1;

        @Override
        public @NonNull Long summon() {
            return exp;
        }

    };
    /**
     * 登录时间戳范围
     */
    public static final ConfType<Long> LOGIN_TIME_RANGE = new ConfType.ConfTypeLong("login_time_range") {
        /**有效期(ms)*/
        @SuppressWarnings("FieldCanBeLocal")
        private final long exp = 1000 * 60;

        @Override
        public @NonNull Long summon() {
            return exp;
        }

    };
    /**
     * JWT私钥
     */
    public static final ConfType<byte[]> JWT_KEY = new ConfType.ConfTypeByteArr("jwt_private_key") {

        /**密钥长度*/
        @SuppressWarnings("FieldCanBeLocal")
        private final int len = 1024;

        @Override
        public byte[] getData(@NonNull Config conf) {
            return cache = conf.getValue();
        }

        @Override
        public synchronized void setByte(@NonNull Config conf, byte @NotNull [] data) {
            conf.setValue(cache = data);
        }

        @Override
        public byte @NonNull [] summon() {
            byte[] data = new byte[len];
            new SecureRandom().nextBytes(data);
            return data;
        }

    };
    /**
     * 公共salt
     */
    public static final ConfType<String> LOGIN_SALT = new ConfType.ConfTypeString("public_salt") {

        @Override
        public @NonNull String summon() {
            return Long.toUnsignedString(new Random().nextLong(), Character.MAX_RADIX) + Long.toUnsignedString(
                new Random().nextLong(), Character.MAX_RADIX);
        }
    };
    /**
     * 验证码VID
     */
    public static final ConfType<String> VAPTCHA_VID = new ConfType.ConfTypeString("vaptcha_vid") {

        @Override
        public @NonNull String summon() {
            return "";
        }
    };
    /**
     * 验证码KEY
     */
    public static final ConfType<String> VAPTCHA_KEY = new ConfType.ConfTypeString("vaptcha_key") {

        @Override
        public @NonNull String summon() {
            return "";
        }
    };
    /**
     * 用户名长度
     */
    public static final ConfType<Integer> USERNAME_LENGTH = new ConfType.ConfTypeInt("username_length") {
        @Override
        public @NonNull Integer summon() {
            return 18;
        }
    };

    public static final ConfType<Long> UNBOX_TIME = new ConfType.ConfTypeLong("unbox_time") {
        @Override
        public @NonNull Long summon() {
            return System.currentTimeMillis();
        }
    };
    /**
     * 邮件内容: 注册邮箱验证码
     */
    public static final ConfType.ConfBox<String,Locale, ConfType.ConfTypeString> MAIL_REGCODE_CONTENT=new ConfType.ConfBox<String, Locale, ConfType.ConfTypeString>() {
    @Override
    protected ConfType.ConfTypeString buildConfType(Locale locale) {
        return new ConfType.ConfTypeString("mail_content_"+locale.toLanguageTag()) {
            @Override
            public @NonNull String summon() {
                return I18n.msg("mail.register-code.content",locale);
            }
        };
    }
};
}
